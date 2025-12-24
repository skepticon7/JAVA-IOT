//package org.example.model;
//
//import org.example.enums.Status;
//import org.example.util.ReadersGenerators;
//
//public class AirQualitySensor extends Device{
//
//
//    public AirQualitySensor(long id , String name , Status status) {
//        super(id , name , status);
//    }
//
//    @Override
//    public double generateValue() {
//        return 0;
//    }
//
//    @Override
//    public void readValue() {
//        System.out.println(ReadersGenerators.airQualityReader(this).read());;
//    }
//}
