package com.github.snowykte0426.peanut.butter.hexagonal.annotation

import com.github.snowykte0426.peanut.butter.hexagonal.annotation.adapter.Adapter
import com.github.snowykte0426.peanut.butter.hexagonal.annotation.port.Port
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.stereotype.Component
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Port(direction = PortDirection.INBOUND)
internal interface TestPort {
    fun process(data: String): String
}

@Port(direction = PortDirection.INBOUND)
internal interface InboundPort

@Port(direction = PortDirection.OUTBOUND)
internal interface OutboundPort

@Port(direction = PortDirection.OUTBOUND)
internal interface BasePort {
    fun execute(): String
}

internal interface ExtendedPort : BasePort {
    fun additionalMethod(): Int
}

@Port(direction = PortDirection.INBOUND)
internal interface UserService {
    fun createUser(name: String): String
    fun findUser(id: Long): String?
}

@Port(direction = PortDirection.OUTBOUND)
internal interface UserRepository {
    fun save(user: String): String
    fun findById(id: Long): String?
}

@Port(direction = PortDirection.INBOUND)
internal interface Service1

@Port(direction = PortDirection.INBOUND)  
internal interface Service2

@Port(direction = PortDirection.OUTBOUND)
internal interface Repository1

@Port(direction = PortDirection.OUTBOUND)
internal interface Repository2

class HexagonalAnnotationTest : FunSpec({

    test("PortDirection enum should have correct values") {
        val values = PortDirection.values()
        
        values.size shouldBe 2
        values shouldBe arrayOf(PortDirection.INBOUND, PortDirection.OUTBOUND)
    }

    test("PortDirection should be usable in comparisons") {
        PortDirection.INBOUND shouldNotBe PortDirection.OUTBOUND
        PortDirection.INBOUND shouldBe PortDirection.INBOUND
        PortDirection.OUTBOUND shouldBe PortDirection.OUTBOUND
    }

    test("Port annotation should be present on annotated interface") {
        val annotation = TestPort::class.findAnnotation<Port>()
        annotation shouldNotBe null
        annotation!!.direction shouldBe PortDirection.INBOUND
    }

    test("Port annotation should support different directions") {
        val inboundAnnotation = InboundPort::class.findAnnotation<Port>()
        val outboundAnnotation = OutboundPort::class.findAnnotation<Port>()

        inboundAnnotation!!.direction shouldBe PortDirection.INBOUND
        outboundAnnotation!!.direction shouldBe PortDirection.OUTBOUND
    }

    test("Adapter annotation should be present on annotated class") {
        @Adapter(direction = PortDirection.INBOUND)
        class TestAdapter {
            fun handleRequest() = "handled"
        }

        val annotation = TestAdapter::class.findAnnotation<Adapter>()
        annotation shouldNotBe null
        annotation!!.direction shouldBe PortDirection.INBOUND
    }

    test("Adapter annotation should support different directions") {
        @Adapter(direction = PortDirection.INBOUND)
        class InboundAdapter

        @Adapter(direction = PortDirection.OUTBOUND)
        class OutboundAdapter

        val inboundAnnotation = InboundAdapter::class.findAnnotation<Adapter>()
        val outboundAnnotation = OutboundAdapter::class.findAnnotation<Adapter>()

        inboundAnnotation!!.direction shouldBe PortDirection.INBOUND
        outboundAnnotation!!.direction shouldBe PortDirection.OUTBOUND
    }

    test("Adapter annotation should be meta-annotated with Component") {
        val adapterAnnotationClass = Adapter::class.java
        val componentAnnotation = adapterAnnotationClass.getAnnotation(Component::class.java)
        
        componentAnnotation shouldNotBe null
    }

    test("Port annotation should have runtime retention") {
        val portAnnotationClass = Port::class.java
        val retentionAnnotation = portAnnotationClass.getAnnotation(Retention::class.java)
        
        retentionAnnotation shouldNotBe null
        retentionAnnotation.value shouldBe RetentionPolicy.RUNTIME
    }

    test("Adapter annotation should have runtime retention") {
        val adapterAnnotationClass = Adapter::class.java
        val retentionAnnotation = adapterAnnotationClass.getAnnotation(Retention::class.java)
        
        retentionAnnotation shouldNotBe null
        retentionAnnotation.value shouldBe RetentionPolicy.RUNTIME
    }

    test("annotations should be discoverable through reflection") {
        @Adapter(direction = PortDirection.OUTBOUND)
        class DatabaseUserAdapter : UserRepository {
            override fun save(user: String) = "saved: $user"
            override fun findById(id: Long) = "User $id"
        }

        UserRepository::class.hasAnnotation<Port>() shouldBe true
        DatabaseUserAdapter::class.hasAnnotation<Adapter>() shouldBe true
    }

    test("annotations should work with inheritance") {
        @Adapter(direction = PortDirection.OUTBOUND)
        abstract class BaseAdapter : BasePort

        class ConcreteAdapter : BaseAdapter() {
            override fun execute() = "executed"
        }

        BasePort::class.hasAnnotation<Port>() shouldBe true
        ExtendedPort::class.hasAnnotation<Port>() shouldBe false
        BaseAdapter::class.hasAnnotation<Adapter>() shouldBe true
        ConcreteAdapter::class.hasAnnotation<Adapter>() shouldBe false
    }

    test("annotations should be accessible in realistic hexagonal architecture scenario") {
        // Web Adapter (Inbound)
        @Adapter(direction = PortDirection.INBOUND)
        class UserController(private val userService: UserService) {
            fun createUser(name: String) = userService.createUser(name)
        }

        // Database Adapter (Outbound)
        @Adapter(direction = PortDirection.OUTBOUND) 
        class DatabaseUserRepository : UserRepository {
            override fun save(user: String) = "saved: $user"
            override fun findById(id: Long) = "user $id"
        }

        // Verify annotations are present and correct
        val userServiceAnnotation = UserService::class.findAnnotation<Port>()
        val userRepositoryAnnotation = UserRepository::class.findAnnotation<Port>()
        val userControllerAnnotation = UserController::class.findAnnotation<Adapter>()
        val databaseRepositoryAnnotation = DatabaseUserRepository::class.findAnnotation<Adapter>()

        userServiceAnnotation!!.direction shouldBe PortDirection.INBOUND
        userRepositoryAnnotation!!.direction shouldBe PortDirection.OUTBOUND
        userControllerAnnotation!!.direction shouldBe PortDirection.INBOUND
        databaseRepositoryAnnotation!!.direction shouldBe PortDirection.OUTBOUND
    }

    test("PortDirection should be serializable and comparable") {
        val inbound = PortDirection.INBOUND
        val outbound = PortDirection.OUTBOUND

        // Test enum properties
        inbound.name shouldBe "INBOUND"
        outbound.name shouldBe "OUTBOUND"
        inbound.ordinal shouldBe 0
        outbound.ordinal shouldBe 1

        // Test comparison
        (inbound.ordinal < outbound.ordinal) shouldBe true
    }

    test("annotations should support multiple instances on different classes") {
        val service1Ann = Service1::class.findAnnotation<Port>()!!
        val service2Ann = Service2::class.findAnnotation<Port>()!!
        val repo1Ann = Repository1::class.findAnnotation<Port>()!!
        val repo2Ann = Repository2::class.findAnnotation<Port>()!!

        service1Ann.direction shouldBe PortDirection.INBOUND
        service2Ann.direction shouldBe PortDirection.INBOUND
        repo1Ann.direction shouldBe PortDirection.OUTBOUND
        repo2Ann.direction shouldBe PortDirection.OUTBOUND
    }
})