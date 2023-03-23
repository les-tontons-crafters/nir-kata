package parse.dont.validate;

import io.vavr.Tuple;
import io.vavr.test.Arbitrary;
import io.vavr.test.Gen;
import io.vavr.test.Property;
import org.junit.jupiter.api.Test;

import java.util.Random;

class NIRProperties {
    private final Random random = new Random();
    private final Gen<Sex> sexGenerator = Gen.choose(Sex.values());
    private final Gen<Year> yearGenerator = Gen.choose(0, 99).map(Year::fromInt);
    private final Gen<Month> monthGenerator = Gen.choose(Month.values());
    private final Gen<Department> departmentGenerator =
            Gen.frequency(Tuple.of(9, Gen.choose(1, 95)),
                            Tuple.of(1, Gen.choose(99)))
                    .map(Department::fromInt);

    private final Gen<City> cityGenerator = Gen.choose(1, 999).map(City::fromInt);
    private final Gen<SerialNumber> serialNumberGenerator = Gen.choose(1, 999).map(SerialNumber::fromInt);
    private final Arbitrary<NIR> validNIR =
            sexGenerator
                    .map(NIRBuilder::new)
                    .map(nirBuilder -> nirBuilder.withYear(yearGenerator.apply(random)))
                    .map(nirBuilder -> nirBuilder.withMonth(monthGenerator.apply(random)))
                    .map(nirBuilder -> nirBuilder.withDepartment(departmentGenerator.apply(random)))
                    .map(nirBuilder -> nirBuilder.withCity(cityGenerator.apply(random)))
                    .map(nirBuilder -> nirBuilder.withSerialNumber(serialNumberGenerator.apply(random)))
                    .map(x -> new NIR(
                                    x.getSex(),
                                    x.getYear(),
                                    x.getMonth(),
                                    x.getDepartment(),
                                    x.getCity(),
                                    x.getSerialNumber()
                            )
                    )
                    .arbitrary();

    @Test
    void roundTrip() {
        Property.def("parseNIR(nir.ToString()) == nir")
                .forAll(validNIR)
                .suchThat(nir -> NIR.parseNIR(nir.toString()).contains(nir))
                .check()
                .assertIsSatisfied();
    }
}