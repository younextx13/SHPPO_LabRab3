package com.configure.entities;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class PcComponent extends Component { //  основной класс компоненты
    String brand;

    public PcComponent(ComponentType type) {
        this.type = type;
    }

    public String configureToString(boolean inernal) { //  вывод конфигурации в строку
        if (configures == null) {
            return "";
        }
        String needRet = inernal?"\n":"";
        StringJoiner stringBuilder = new StringJoiner(inernal?"\n":", ");
        for (Configure configure : configures) {
            stringBuilder.add(
                    "["+needRet+"  require='" + configure.require + "'"+
                            needRet+"  socket ='" + configure.type + (inernal? needRet+"  connect = "+configure.connect:"")+needRet+"   ],");

        }
        return stringBuilder.toString();
    }

    public String characteristicToString() { //  вывод характеристик в строку
        if (characteristics == null) {
            return "";
        }
        StringJoiner stringBuilder = new StringJoiner(",\n");
        for (Characteristic characteristic : characteristics) {
            stringBuilder.add(
                    "['" + characteristic.type +
                            "='" + characteristic.value + "']");
        }
        return stringBuilder.toString();
    }

    public Configure getConfig(SocketType socket) { //  не используется
        for (Configure configure : configures) {
            if (configure.type == socket) {
                return configure;
            }
        }
        return null;
    }

    public Configure getAnyConfig(SocketType[] socket) { // получить конфиг по любому сокету из списка
        List<SocketType> socketList = Arrays.asList(socket);
        for (Configure configure : configures) {
            if (socketList.contains(configure.type)) {
                return configure;
            }
        }
        return null;
    }

    public PcComponent connect(PcComponent connected) { // соединяем компоненты
        PcComponent test = this.clone();// делаем копию основной компоненты
        PcComponent test1 = connected.clone();// соеденительной
        for (Configure thisCfg : test.configures) {
            for (Configure configure : test1.configures) {
                if(thisCfg.connect == null && thisCfg.input && configure.input && thisCfg.type == configure.type) { // если ещё не подключенно оба являются первичнымим сокетами и сокеты совпадают
                    thisCfg.connect = test1; // делаем перекрёсную ссылку
                    configure.connect= test;
                    return  test; // возвращаем новый объект
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return toString(false);
    }
    public String toString(boolean inernal) {
        return "PcComponent(" + type + "){" +
                "brand='" + brand + '\'' +
                "name='" + name + '\'' +
                "configure='" + configureToString(inernal) + '\'' +
                "characteristic='" + characteristicToString() + '\'' +
                '}';
    }

    public static class PcComponentBuilder { // билдер для сборки компоненты
        private PcComponent component; // свойство для компоненты

        public PcComponentBuilder(ComponentType type) {
            component = new PcComponent(type);
        } // по типу создаём компоненту

        public PcComponentBuilder setId(String id) { //задать id
            component.id = id;
            return this;
        }

        public PcComponentBuilder setName(String name) { // задать имя
            component.name = name;
            return this;
        }

        public PcComponentBuilder setBrand(String brand) { // задать брэнд
            component.brand = brand;
            return this;
        }

        public PcComponentBuilder addConfigure(String socketType, Boolean require, Boolean input) { // добавить конфигурацию
            Configure cfg = new Configure();
            cfg.type = SocketType.valueOf(socketType);
            cfg.require = require;
            cfg.input = input;
            component.configures.add(cfg);
            return this;
        }

        public PcComponentBuilder addConfigure(String socketType, Boolean require) {
            return addConfigure(socketType, require, false);
        }

        public PcComponentBuilder addConfigure(String socketType) {
            return addConfigure(socketType, false, false);
        }

        public PcComponentBuilder addCharacteristic(CharacteristicType type, String value) {// добавление характеристики
            Characteristic chrs = new Characteristic();
            chrs.type = type;
            chrs.value = value;
            component.characteristics.add(chrs);
            return this;
        }

        public PcComponentBuilder addCharacteristic(String characteristicType, String value) {
            return addCharacteristic(CharacteristicType.valueOf(characteristicType), value);
        }

        public PcComponent build() {
            return component;
        } // вернут сконфигуренный файл
    }

    @Override
    public PcComponent clone() { // сделать копию объекта
        PcComponent ret = new PcComponent(this.type);
        ret.id = this.id;
        ret.name = this.name;
        ret.brand = this.brand;
        ret.configures = this.configures.stream().map(Configure::new).collect(Collectors.toCollection(LinkedList::new));
        ret.characteristics = this.characteristics.stream().map(Characteristic::new).collect(Collectors.toCollection(LinkedList::new));
        return ret;
    }
}
