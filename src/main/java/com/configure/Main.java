package com.configure;

import com.configure.entities.ComponentType;
import com.configure.entities.PcComponent;
import com.configure.factory.FactoryMethodComponent;
import com.configure.service.ComponentReader;
import com.configure.service.ComponentValidator;
import com.configure.service.ComponentWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

@ComponentScan(basePackages = "com.configure")// корневой пакет для сканирвоания бинов
public class Main { // основная функция
    @Autowired // магия
    ComponentReader componentReader; // сервис для чтения с файлов
    @Autowired // магия
    ComponentWriter componentWriter; // сервис для записи с консоли
    @Autowired// магия
    ComponentValidator componentValidator; // сервис для проверки на вал идность

    public static void main(String[] args) throws InterruptedException {// стартовая функция
        ApplicationContext context = new AnnotationConfigApplicationContext(Main.class); // подготовка к магии
        Main p = context.getBean(Main.class);// загрузка контекста магии
        p.start();// вызыв функции start из текущего класса
    }

    void start() throws InterruptedException { // просто запускает приложение
        Map<ComponentType, LinkedList<PcComponent>> components = new HashMap<>(); // карта для компонент

        try {
            components.put(ComponentType.CPU, componentReader.readCpu()); // читаем с файла процы
            components.put(ComponentType.MOTHERBOARD, componentReader.readMotherboard()); // читаем с файла материнки
            components.put(ComponentType.GPU, componentReader.readGraphiccards()); // читаем с файла видяхи
            components.put(ComponentType.RAM, componentReader.readRam()); // читаем с файла оперативки
            components.put(ComponentType.HDD, componentReader.readRom()); // харды
        } catch (FileNotFoundException e) {
            System.out.println("Дальнейшая сборка невозможна"); // если выпало исключение то скорее всего нихрена не собёрм в будущем
            return;
        }

        componentWriter.startMenu(components); // запускаем консольное меню для пользователя

        LinkedList<PcComponent> allConfig = new LinkedList<>(components.get(ComponentType.MOTHERBOARD)); // все возможные конфигурации
        CombinationTheard[] theards = new CombinationTheard[6];
        int counter = 0;
        for (ComponentType componentType : new ComponentType[]{ComponentType.CPU, ComponentType.GPU, ComponentType.RAM, ComponentType.PSU, ComponentType.HDD, ComponentType.SSD})  // проходимся по всем типам компонентам
        {
            theards[counter++] = new CombinationTheard(allConfig, components, componentType);
        }

        long time = System.nanoTime();
        for (CombinationTheard theard : theards) {
            theard.start();
            theard.join();
        }
        System.out.println("start validation");
        for (PcComponent component : allConfig) { // проходимся по всем конфигам
            ComponentValidator.ValidateResult errors = componentValidator.validate(component, true); // проверяем их на валидность
            if (errors.getErrors().length > 0) { // если есть ошибки то сборка не валидная
                continue;
            }
            System.out.println("_________________________СБОРКА_________________________" + component.toString(true)); // выводим в консоль
            for (ComponentValidator.ValidateWarning validateWarning : errors.getWarning()) { // если есть предупреждения (а их не будет)
                System.out.println(validateWarning.getMessage());// вывести сообщение
            }
            System.out.println("\n");// просто пустые линии
        }
        time = System.nanoTime() - time;
        System.out.printf("Elapsed %,9.3f ms\n", time/1_000_000.0);

    }

}
