package com.company;

import java.text.DecimalFormat;
import java.util.*;

public class TaskCensus {
    public void start(){
        double processTime;
        List<Person> people = generatePeople(12_000_000);

        processTime = processSingleStream(people);
        System.out.println("Process time: " + processTime + " s\n");
//        Process time: 0.6840363 s
        processTime = processParallelStreams(people);
        System.out.println("Process time: " + processTime + " s\n");
//        Process time: 0.2338744 s

        int[] numbers = {100, 1_000, 10_000, 100_000, 1_000_000, 10_000_000};

        for (int number : numbers){
            people = generatePeople(number);
            double timeSingle = processSingleStream(people);
            double timeParallel = processParallelStreams(people);
            double percent = Math.round((timeSingle - timeParallel) / timeSingle * 100);
            System.out.println("single: " + timeSingle + " s, parallel: " + timeParallel + " s, percent: " + percent + "\n");
        }
        System.out.println("Processor: i5");
    }

    private double processParallelStreams(List<Person> people) {
        long startTime = System.nanoTime();

        System.out.println("Военнообязанных:");
        System.out.println(people.parallelStream().filter(x -> x.getSex() == Sex.MAN).filter(x -> x.getAge() >= 18 && x.getAge() <= 27).count());

        System.out.println("Средний возраст мужчин:");
        // Если одни только женщины, исключение вылетает, потому что пустое значение хотим конвертировать в double.
        OptionalDouble avg = people.parallelStream().filter(x -> x.getSex() == Sex.MAN).mapToInt(x -> x.getAge()).average();
        System.out.println(avg.isPresent() ? (new DecimalFormat("0.00")).format(avg.getAsDouble()) : "no man");

        System.out.println("Кол-во потенциально работоспособных людей:");
        System.out.println(people.parallelStream().
                filter(x -> x.getAge() >= 18 && (x.getSex() == Sex.MAN ? x.getAge() < 65 : x.getAge() < 60)).
                count());

        long stopTime = System.nanoTime();
        return (double) (stopTime - startTime) / 1_000_000_000.0;
    }

    private double processSingleStream(List<Person> people) {
        long startTime = System.nanoTime();

        System.out.println("Военнообязанных:");
        System.out.println(people.stream().filter(x -> x.getSex() == Sex.MAN).filter(x -> x.getAge() >= 18 && x.getAge() <= 27).count());

        System.out.println("Средний возраст мужчин:");
        // Если одни только женщины, исключение вылетает, потому что пустое значение хотим конвертировать в double.
        OptionalDouble avg = people.stream().filter(x -> x.getSex() == Sex.MAN).mapToInt(x -> x.getAge()).average();
        System.out.println(avg.isPresent() ? (new DecimalFormat("0.00")).format(avg.getAsDouble()) : "no man");

        System.out.println("Кол-во потенциально работоспособных людей:");
        System.out.println(people.stream().
                filter(x -> x.getAge() >= 18 && (x.getSex() == Sex.MAN ? x.getAge() < 65 : x.getAge() < 60)).
                count());

        long stopTime = System.nanoTime();
        return (double) (stopTime - startTime) / 1_000_000_000.0;
    }

    private List<Person> generatePeople(int count) {
        List<String> names = Arrays.asList("Иванов", "Петров", "Сидоров");
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            people.add(new Person(names.get(
                    new Random().nextInt(names.size())),
                    new Random().nextInt(100),
                    Sex.randomSex()));
        }
        return people;
    }
}
