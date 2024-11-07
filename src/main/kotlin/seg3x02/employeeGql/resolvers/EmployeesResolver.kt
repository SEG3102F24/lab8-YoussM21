package seg3x02.employeeGql.resolvers

import org.springframework.stereotype.Controller
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput
import seg3x02.employeeGql.resolvers.types.UpdateEmployeeInput
import java.util.UUID

@Controller
class EmployeesResolver(
    private val employeesRepository: EmployeesRepository
) {
    @QueryMapping
    fun employees(): List<Employee> {
        return employeesRepository.findAll()
    }

    @QueryMapping
    fun employeeById(@Argument id: String): Employee? {
        return employeesRepository.findById(id).orElse(null)
    }

    @MutationMapping
    fun createEmployee(@Argument input: CreateEmployeeInput): Employee {
        if (input.name != null && 
            input.dateOfBirth != null && 
            input.city != null && 
            input.salary != null) {
            
            val employee = Employee(
                name = input.name,
                dateOfBirth = input.dateOfBirth,
                city = input.city,
                salary = input.salary,
                gender = input.gender,
                email = input.email
            )
            employee.id = UUID.randomUUID().toString()
            return employeesRepository.save(employee)
        } else {
            throw IllegalArgumentException("Required fields cannot be null")
        }
    }

    @MutationMapping
    fun updateEmployee(@Argument id: String, @Argument input: UpdateEmployeeInput): Employee? {
        val existingEmployee = employeesRepository.findById(id).orElse(null)
        if (existingEmployee != null) {
            val updatedEmployee = existingEmployee.copy(
                name = input.name ?: existingEmployee.name,
                dateOfBirth = input.dateOfBirth ?: existingEmployee.dateOfBirth,
                city = input.city ?: existingEmployee.city,
                salary = input.salary ?: existingEmployee.salary,
                gender = input.gender ?: existingEmployee.gender,
                email = input.email ?: existingEmployee.email
            )
            updatedEmployee.id = existingEmployee.id
            return employeesRepository.save(updatedEmployee)
        }
        return null
    }

    @MutationMapping
    fun deleteEmployee(@Argument id: String): Boolean {
        return if (employeesRepository.existsById(id)) {
            employeesRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}