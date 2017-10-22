package lambda.part3.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"WeakerAccess"})
public class Mapping {

    private static class MapHelper<T> {

        private final List<T> list;

        public MapHelper(List<T> list) {
            this.list = list;
        }

        public List<T> getList() {
            return list;
        }

        // ([T], T -> R) -> [R]
        public <R> MapHelper<R> map(Function<T, R> f) {
            // TODO
            //throw new UnsupportedOperationException();
            List<R> result = new ArrayList<>(list.size());
            for (T element : list) {
                result.add(f.apply(element));
            }
            return new MapHelper<>(result);
        }

        // ([T], T -> [R]) -> [R]
        public <R> MapHelper<R> flatMap(Function<T, List<R>> f) {
            // TODO
            //throw new UnsupportedOperationException();
            List<R> result = new ArrayList<>(list.size());
            for (T element : list) {
                result.addAll(f.apply(element));
            }
            return new MapHelper<>(result);
        }
    }

    private List<JobHistoryEntry> addOneYear(List<JobHistoryEntry> jobs) {
        List<JobHistoryEntry> resultJobs = new ArrayList<>();
        for (JobHistoryEntry job : jobs) {
            resultJobs.add(job.withDuration(job.getDuration() + 1));
        }
        return resultJobs;
    }

    private List<JobHistoryEntry> replaceqa(List<JobHistoryEntry> jobs) {
        List<JobHistoryEntry> resultJobs = new ArrayList<>();
        for (JobHistoryEntry job : jobs) {
            if (job.getPosition().equals("qa")) {
                resultJobs.add(job.withPosition("QA"));
            } else {
                resultJobs.add(job);
            }
        }
        return resultJobs;
    }

    @Test
    public void mapping() {
        List<Employee> employees = getEmployees();


        List<Employee> mappedEmployees = new MapHelper<>(employees)
                .map(employee -> employee.withPerson(employee.getPerson().withFirstName("John")))
                .map(employee -> employee.withJobHistory(addOneYear(employee.getJobHistory())))
                .map(employee -> employee.withJobHistory(replaceqa(employee.getJobHistory())))
                /*
                .map(TODO) // Изменить имя всех сотрудников на John .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                .map(TODO) // Добавить всем сотрудникам 1 год опыта .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                .map(TODO) // Заменить все qa на QA
                * */
                .getList();

        List<Employee> expectedResult = Arrays.asList(
                new Employee(new Person("John", "Galt", 30),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(new Person("John", "Doe", 40),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(new Person("John", "White", 50),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );

        assertEquals(mappedEmployees, expectedResult);
    }

    private static class LazyMapHelper<T, R> {

        private List<T> list;
        private Function<T, R> function;

        public LazyMapHelper(List<T> list, Function<T, R> function) {
            this.list = list;
            this.function = function;
        }

        public static <T> LazyMapHelper<T, T> from(List<T> list) {
            return new LazyMapHelper<>(list, Function.identity());
        }

        public List<R> force() {
            // TODO
            //throw new UnsupportedOperationException();
            List<R> result = new ArrayList<>();
            for (T element : list) {
                result.add(this.function.apply(element));

            }
            return result;
        }

        public <R2> LazyMapHelper<T, R2> map(Function<R, R2> f) {
            // TODO
            //throw new UnsupportedOperationException();
            Function<T, R2> funcT2 = t -> {
                return f.apply(function.apply(t));
            };
            return new LazyMapHelper<>(list, funcT2);
        }
    }

/* Done at lesson 17.10
    private static class LazyFlatMapHelper<T, R> {
        private final List<T> list;
        private final Function<T, List<R>> mapper;

        public LazyFlatMapHelper(List<T> list, Function<T, List<R>> mapper) {
            this.list = list;
            this.mapper = mapper;
        }

        public static <T> LazyFlatMapHelper<T, T> from(List<T> list) {
            return new LazyFlatMapHelper<>(list, t -> Collections.singletonList(t));
        }

        public <U> LazyFlatMapHelper<T, U> flatMap(Function<R, List<U>> remapper) {
            return new LazyFlatMapHelper<>(list, mapper.andThen(result -> result.stream()
                    .flatMap(element -> remapper.apply(element).stream())
                    .collect(Collectors.toList())));
        }

        public List<R> force() {
            ArrayList<R> result = new ArrayList<>();
            list.stream().map(value ->mapper.apply(value));
            return result;
        }
    }
    */
    private static class LazyFlatMapHelper<T, R> {

        private final List<T> list;
        private final Function<T, List<R>> mapper;

        private LazyFlatMapHelper(List<T> list, Function<T, List<R>> mapper) {
            this.list = list;
            this.mapper = mapper;
        }

        public static <T> LazyFlatMapHelper<T, T> from(List<T> list) {
            return new LazyFlatMapHelper<>(list, Collections::singletonList);
        }

        public <U> LazyFlatMapHelper<T, U> flatMap(Function<R, List<U>> remapper) {
            return new LazyFlatMapHelper<>(list, mapper.andThen(result -> force(result, remapper)));
        }

        public List<R> force() {
            return force(list, mapper);
        }

        private <A, B> List<B> force(List<A> list, Function<A, List<B>> mapper) {
            List<B> result = new ArrayList<>(list.size());
            list.forEach(element -> result.addAll(mapper.apply(element)));
            return result;
        }
    }

    @Test
    public void lazyFlatMapping() {
        List<Employee> employees = getEmployees();
        List<JobHistoryEntry> force = LazyFlatMapHelper.from(employees)
                                                       .flatMap(Employee::getJobHistory)
                                                       .force();

        List<Character> force1 = LazyFlatMapHelper.from(employees)
                                                  .flatMap(Employee::getJobHistory)
                                                  .flatMap(entry -> entry.getPosition()
                                                                         .chars()
                                                                         .mapToObj(sym -> (char) sym)
                                                                         .collect(Collectors.toList()))
                                                  .force();


        System.out.println();

    }

    @Test
    public void lazyMapping() {
        List<Employee> employees = getEmployees();

        List<Employee> mappedEmployees = LazyMapHelper.from(employees)
                .map(employee -> employee.withPerson(employee.getPerson().withFirstName("John")))
                .map(employee -> employee.withJobHistory(addOneYear(employee.getJobHistory())))
                .map(employee -> employee.withJobHistory(replaceqa(employee.getJobHistory())))
                /*
                .map(TODO) // Изменить имя всех сотрудников на John .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                .map(TODO) // Добавить всем сотрудникам 1 год опыта .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                .map(TODO) // Заменить все qu на QA
                */
                .force();

        List<Employee> expectedResult = Arrays.asList(
                new Employee(new Person("John", "Galt", 30),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(new Person("John", "Doe", 40),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(new Person("John", "White", 50),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );

        assertEquals(mappedEmployees, expectedResult);
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
            new Employee(
                new Person("a", "Galt", 30),
                Arrays.asList(
                        new JobHistoryEntry(2, "dev", "epam"),
                        new JobHistoryEntry(1, "dev", "google")
                )),
            new Employee(
                new Person("b", "Doe", 40),
                Arrays.asList(
                        new JobHistoryEntry(3, "qa", "yandex"),
                        new JobHistoryEntry(1, "qa", "epam"),
                        new JobHistoryEntry(1, "dev", "abc")
                )),
            new Employee(
                new Person("c", "White", 50),
                Collections.singletonList(
                        new JobHistoryEntry(5, "qa", "epam")
                ))
        );
    }
}
