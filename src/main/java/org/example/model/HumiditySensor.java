//package org.example.model;
//
//import org.example.enums.Status;
//import org.example.util.ReadersGenerators;
//
//public class HumiditySensor extends Device{
//
//    public HumiditySensor(long id , String name , Status isActive) {
//        super(id, name, isActive);
//    }
//
//    public HumiditySensor(){
//        super();
//    }
//
//    @Override
//    public double generateValue() {
//
//        return 0;
//    }
//
//    @Override
//    public void readValue() {
//        System.out.println(ReadersGenerators.humidityReader(this).read());;
//    }
//
//
//}
