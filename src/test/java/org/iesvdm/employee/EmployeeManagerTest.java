package org.iesvdm.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class EmployeeManagerTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BankService bankService;

    /**
     * Explica en este comentario que efecto tiene
     * esta anotacion @InjectMocks
     */
    @InjectMocks
    private EmployeeManager employeeManager;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    @Captor
    private ArgumentCaptor<Double> amountCaptor;

    @Spy
    private Employee notToBePaid = new Employee("1", 1000);

    @Spy
    private Employee toBePaid = new Employee("2", 2000);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Descripcion del test:
     * Crea un stub when-thenReturn para employeeRepository.findAll
     * que devuelva una coleccion vacia.
     * Comprueba que al invocar employeeManagar.payEmployees
     * con el stub anterior no se paga a ningun empleado.
     */
    @Test
    public void testPayEmployeesReturnZeroWhenNoEmployeesArePresent() {
        when(employeeRepository.findAll()).thenReturn(asList());
    }

    /**
     * Descripcion del test:
     * Crea un stub when-thenReturn que devuelva una lista con un empleado
     * para employeeRepository.findAll.
     * Comprueba que al invocar employeeManager.payEmployess con el stub
     * anterior se paga a un empleado.
     * Tambien comprueba con verify que se hace una llamada a bankService.pay
     * con los datos de pago del Employ del stub when-thenReturn inicialmente
     * creado.
     */
    @Test
    public void testPayEmployeesReturnOneWhenOneEmployeeIsPresentAndBankServicePayPaysThatEmployee() {
        Employee employee = new Employee("1", 1000);
        when(employeeRepository.findAll()).thenReturn(asList(employee));

        employeeManager.payEmployees();

        verify(bankService).pay("1", 1000);
    }


    /**
     * Descripcion del test:
     * Crea un stub when-thenReturn para employeeRepository.findAll
     * que devuelva una coleccion con 2 empleados diferentes.
     * Comprueba que employeeManager.payEmployees paga a 2 empleados.
     * Verifica la interaccion (con verify) de que se hacen 2 invocaciones
     * con las caracteristicas de pago de cada Employee que creaste en el stub
     * primero when-thenReturn.
     * Por último, verificea que no hay más interacciones con el mock de bankService
     * -pista verifyNoiMoreInteractions.
     */
    @Test
    public void testPayEmployeesWhenSeveralEmployeeArePresent() {
        when(employeeRepository.findAll()).thenReturn(asList(new Employee("1", 1000), new Employee("2", 2000)));
        employeeManager.payEmployees();
        verify(bankService).pay("1", 1000);
        verify(bankService).pay("2", 2000);
        verifyNoMoreInteractions(bankService);
    }

    /**
     * Descripcion del test:
     * Crea un stub when-thenReturn para employeeRepository.findAll
     * que devuelva una coleccion de 2 empleados.
     * Comprueba que cuando llamas a employeeManager.payEmployee pagas a 2 empleados.
     * Para el mock de bankService mediante InOrder e inOrder.verify verifica
     * que se pagan en orden a los 2 empleados con sus caracteristicas invocando
     * a pay en el orden de la coleccion.
     * Por ultimo, verifica que despues de pagar no hay mas interacciones.
     */
    @Test
    public void testPayEmployeesInOrderWhenSeveralEmployeeArePresent() {
        when(employeeRepository.findAll()).thenReturn(asList
                (new Employee("1", 1000), new Employee("2", 2000)));
        employeeManager.payEmployees();
        inOrder().verify(bankService).pay("1",1000);
        inOrder().verify(bankService).pay("2", 2000);
        verifyNoMoreInteractions(bankService);

    }

    /**
     * Descripcion del test:
     * Misma situacion que el test anterior solo que al inOrder le aniades tambien employeeRepository
     * para verificar que antes de hacer el pago bankService.pay para cada empleado
     * se realiza la invocacion de employeeRepository.findAll.
     * Pista: utiliza un InOrder inOrder = inOrder(bankService, employeeRepository) para
     * las verificaciones (verify).
     */
    @Test
    public void testExampleOfInOrderWithTwoMocks() {
         when(employeeRepository.findAll()).thenReturn(asList(new Employee("1", 1000), new Employee("2", 2000)));
            employeeManager.payEmployees();
            inOrder().verify(employeeRepository).findAll();
            inOrder().verify(bankService).pay("1", 1000);
            inOrder().verify(bankService).pay("2", 2000);
            verifyNoMoreInteractions(bankService);

    }


    /**
     * Descripcion del test:
     * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
     * una coleccion con 2 Employee diferentes. Comprueba que employeesManager.payEmployees paga
     * a 2 Employee.
     * Seguidamente utiliza los Captor: idCaptor y amountCaptor para capturar todos los
     * id's y amounts que se han invocado cuando has comprobado que employManager.payEmployees pagaba a 2,
     * sobre el mock de bankService en un verify para el metodo pay -puedes aniadir cuantas veces se invoco
     * al metodo pay en el VerificationMode.
     * Comprueba los valores de los captor accediendo a ellos mediante captor.getAllValues y comparando
     * con lo que se espera.
     * Por ultimo verifica que no hay mas interacciones con el mock de bankService.
     */
    @Test
    public void testExampleOfArgumentCaptor() {
        when(employeeRepository.findAll()).thenReturn(asList
                (new Employee("1", 1000), new Employee("2", 2000)));
        employeeManager.payEmployees();
        verify(bankService, times(2)).pay(idCaptor.capture(), amountCaptor.capture());
        assertThat(idCaptor.getAllValues()).containsExactly("1", "2");
        assertThat(amountCaptor.getAllValues()).containsExactly(1000.0, 2000.0);
        verifyNoMoreInteractions(bankService);


    }

    /**
     * Descripcion del test:
     * Utiliza el spy toBePaid de los atributos de esta clase de test para
     * crear un stub when-thenReturn con 1 solo Employee.
     * Comprueba que al invocar a employeeManager.payEmployees solo paga a 1 Employee.
     * Por ultimo, mediante un inOrder para 2 mocks: InOrder inOrder = inOrder(bankService, toBePaid)
     * verifica que la interaccion se realiza en el orden de bankService.pay las caracteristicas
     * del Employee toBePaid y a continuacion verifica tambien que se invoca toBePaid.setPaid true.
     */
    @Test
    public void testEmployeeSetPaidIsCalledAfterPaying() {
        when(employeeRepository.findAll()).thenReturn(asList(toBePaid));
        employeeManager.payEmployees();
        InOrder inOrder = inOrder(bankService, toBePaid);
        inOrder.verify(bankService).pay("2", 2000);
        inOrder.verify(toBePaid).setPaid(true);
    }


    /**
     * Descripcion del test:
     * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
     * una coleccion solo con el spy de atributo de la clase notToBePaid.
     * Seguidamente, crea un stub doThrow-when para bankService.pay con ArgumentMatcher
     * any como entradas para el metodo pay. La exception a lanzar sera una RuntimeException
     * Comprueba que cuando invocas employeeManager.payEmployees con bankService lanzando
     * una RuntimeException en el stub anterior, los Employee pagados son 0.
     * Tambien, verifica sobre el spy notToBePaid que se llamo a setPaid false como
     * efecto de no pago.
     */
    @Test
    public void testPayEmployeesWhenBankServiceThrowsException() {
        when(employeeRepository.findAll()).thenReturn(asList(notToBePaid));
        doThrow(new RuntimeException()).when(bankService).pay(any(), anyDouble());
        employeeManager.payEmployees();
        verify(notToBePaid).setPaid(false);
        assertThat(notToBePaid.isPaid()).isFalse();



    }

    /**
     * Descripcion del test:
     * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
     * una coleccion 2 Employee con el spy de atributo de la clase notToBePaid y toBePaid.
     * Seguidamente, crea un stub con encademaniento para 2 llamadas doThrow.doNothing-when
     * para bankService.pay de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
     * y en la segunda invocacion de pay (para toBePaid) no haga nada. El metodo pay acepta cualquier argumento
     * indicado mediante ArgumentMatcher any.
     * Comprueba que al invocar employeeManager.payEmployees se paga a solo 1 Employee.
     * A continuacion, verifica las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
     * para el que se lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion que si recibe el pago
     * chequeando la interaccion con el metodo setPaid a false y true respectivamente.
     */
    @Test
    public void testOtherEmployeesArePaidWhenBankServiceThrowsException() {
        when(employeeRepository.findAll()).thenReturn(asList(notToBePaid, toBePaid));
        doThrow(new RuntimeException()).doNothing().when(bankService).pay(any(), anyDouble());
        employeeManager.payEmployees();
        verify(notToBePaid).setPaid(false);
        verify(toBePaid).setPaid(true);
    }


    /**
     * Descripcion del test:
     * Crea un stub when-thenReturn para employeeRepository.findAll que devuelva
     * una coleccion 2 Employee con el spy de atributo de la clase notToBePaid y toBePaid.
     * Seguidamente, crea un stub con encademaniento para 2 llamadas doThrow-when emplea argThat
     * argThat(s -> s.equals("1")), anyDouble como firma de invocacion en el stub para pay
     * de modo que en la primera invocacion de pay (para notToBePaid) se lance una RuntimeException
     * y en la segunda invocacion de pay (para toBePaid) no haga nada. El metodo pay acepta cualquier argumento
     * indicado mediante ArgumentMatcher any.
     * Comprueba que al invocar employeeManager.payEmployees se paga a solo 1 Employee.
     * A continuacion, verifica las interacciones (verify) sobre el spy notToBePaid primer mock de la coleccion
     * para el que se lanza la RuntimeException y el spy toBePaid segundo mock de la coleccion que si recibe el pago
     * chequeando la interaccion con el metodo setPaid a false y true respectivamente.
     */
    @Test
    public void testArgumentMatcherExample() {

    }

}
