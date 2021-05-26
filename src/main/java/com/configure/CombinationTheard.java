package com.configure;

import com.configure.entities.ComponentType;
import com.configure.entities.PcComponent;

import java.util.LinkedList;
import java.util.Map;

public class CombinationTheard extends Thread {

    LinkedList<PcComponent> allConfig;
    ComponentType type;
    Map<ComponentType, LinkedList<PcComponent>> components;

    CombinationTheard(LinkedList<PcComponent> _c, Map<ComponentType, LinkedList<PcComponent>> comps, ComponentType t){
        allConfig = _c;
        type = t;
        components = comps;
    }

    @Override
    public void run() {
        System.out.println("Start for:"+type);
        for (int i = 0; i < allConfig.size(); i++) { // проходимся по всем конфигам
            PcComponent motherboard = allConfig.get(i); // берём мать
            PcComponent mTemp = motherboard.clone(); // делаем её копию
            if (components.get(type) == null) // если компоненты нету игнорим
            {
                // System.out.println("End for:"+type);
                return;
            }
            for (PcComponent cpu : components.get(type)) { // проходимся по всем компонентам одного типа
                PcComponent cTemp = cpu.clone(); // делаем копию
                PcComponent n = mTemp.connect(cTemp); // пытаемся подключить
                if (n != null) { // если успешно
                    allConfig.add(n); // то добавляем в список конфигов
                }
            }
        }
        System.out.println("End for:"+type);
    }
}
