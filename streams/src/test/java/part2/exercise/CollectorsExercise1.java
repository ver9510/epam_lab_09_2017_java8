package part2.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class CollectorsExercise1 {

    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;

        public PersonPositionDuration(Person person, String position, int duration) {
            this.person = person;
            this.position = position;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }

        public int getDuration() {
            return duration;
        }
    }

    private class PersonEmployerPair{
        private String employer;
        private String person;

        public PersonEmployerPair(String employer, String person) {
            this.employer = employer;
            this.person = person;
        }

        public String getEmployer() {
            return employer;
        }

        public String getPerson() {
            return person;
        }
    }

    // "epam" -> "Alex Ivanov 23, Semen Popugaev 25, Ivan Ivanov 33"
    @Test
    public void getEmployeesByEmployer() {
        Map<String, String> result =
        getEmployees().stream().flatMap(employee->employee.getJobHistory().stream().map(job->new PersonEmployerPair(job.getEmployer(),
                employee.getPerson().getFirstName()+" "+employee.getPerson().getLastName()+" "+employee.getPerson().getAge())))
                .collect(Collectors.groupingBy(PersonEmployerPair::getEmployer,Collectors.mapping(PersonEmployerPair::getPerson, Collectors.joining(","))));

    }

    @Test
    public void getTheCoolestOne() {
        Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
        Person coolestQA = new Person("John", "White", 22);
        Person coolestDev = new Person("John", "Galt", 20);
        Person coolestBA = new Person("John", "White", 28);
        assertEquals(coolestQA, coolestByPosition.get("QA"));
        assertEquals(coolestDev, coolestByPosition.get("dev"));
        assertEquals(coolestBA, coolestByPosition.get("BA"));
    }

    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        // First option
        // Collectors.maxBy
        // Collectors.collectingAndThen
        // Collectors.groupingBy
        Stream<PersonPositionDuration> personPositionDurationStream = employees.stream()
                .flatMap(employee -> employee.getJobHistory().stream()
                        .collect(Collectors.groupingBy(JobHistoryEntry::getPosition, Collectors.summingInt(JobHistoryEntry::getDuration)))
                        .entrySet().stream().map(positionDuration -> new PersonPositionDuration(employee.getPerson(), positionDuration.getKey(), positionDuration.getValue())));
        return personPositionDurationStream
                                .collect(Collectors.groupingBy(PersonPositionDuration::getPosition,
                                        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(PersonPositionDuration::getDuration)),x->x.get().getPerson())));
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("John", "Galt", 20),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 21),
                        Arrays.asList(
                                new JobHistoryEntry(4, "BA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 22),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 23),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 24),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 25),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 26),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Bob", "Doe", 27),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 28),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "BA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 29),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 30),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(5, "dev", "abc")
                        )),
                new Employee(
                        new Person("Bob", "White", 31),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

}
