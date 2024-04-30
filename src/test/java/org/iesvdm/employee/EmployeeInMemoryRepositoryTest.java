package org.iesvdm.employee;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test doubles that are "fakes" must be tested
 *
 *
 */
public class EmployeeInMemoryRepositoryTest {

	private EmployeeInMemoryRepository employeeRepository;

	private List<Employee> employees;

	@BeforeEach
	public void setup() {
		employees = new ArrayList<>();
		employeeRepository = new EmployeeInMemoryRepository(employees);
	}

	/**
	 * Descripcion del test:
	 * crea 2 Employee diferentes
	 * aniadelos a la coleccion de employees
	 * comprueba que cuando llamas a employeeRepository.findAll
	 * obtienes los empleados aniadidos en el paso anterior
	 */
	@Test
	public void testEmployeeRepositoryFindAll() {
		Employee employee1 = new Employee("1",  1000);
		Employee employee2 = new Employee("2", 	 2000);
		employees.addAll(asList(employee1, employee2));

		List<Employee> result = employeeRepository.findAll();

		assertThat(result).containsExactly(employee1, employee2);
	}

	/**
	 * Descripcion del test:
	 * salva un Employee mediante el metodo
	 * employeeRepository.save y comprueba que la coleccion
	 * employees contiene solo ese Employee
	 */
	@Test
	public void testEmployeeRepositorySaveNewEmployee() {
		Employee employee = new Employee("1", 1000);

		employeeRepository.save(employee);

		assertThat(employees).containsExactly(employee);
	}

	/**
	 * Descripcion del tets:
	 * crea un par de Employee diferentes
	 * aniadelos a la coleccion de employees.
	 * A continuacion, mediante employeeRepository.save
	 * salva los Employee anteriores (mismo id) con cambios
	 * en el salario y comprueba que la coleccion employees
	 * los contiene actualizados.
	 */
	@Test
	public void testEmployeeRepositorySaveExistingEmployee() {
		Employee employee1 = new Employee("1", 1000);
		Employee employee2 = new Employee("2", 2000);
		employees.addAll(asList(employee1, employee2));

		Employee updatedEmployee1 = new Employee("1", 3000);
		Employee updatedEmployee2 = new Employee("2", 4000);
		employeeRepository.save(updatedEmployee1);
		employeeRepository.save(updatedEmployee2);

		assertThat(employees).containsExactly(updatedEmployee1, updatedEmployee2);
	}
}
