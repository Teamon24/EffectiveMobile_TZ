package org.effective_mobile.task_management_system.database.repository

import org.effective_mobile.task_management_system.database.entity.AbstractEntity
import org.effective_mobile.task_management_system.database.entity.Privilege
import org.effective_mobile.task_management_system.database.entity.Role
import org.effective_mobile.task_management_system.utils.enums.UserRole
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.transaction.annotation.Transactional


@DataJpaTest
@Transactional
open class PrivilegeRepositoryTest {
    @Autowired lateinit var privilegeRepository: PrivilegeRepository
    @Autowired lateinit var roleRepository: RoleRepository
    @Autowired lateinit var testEntityManager: TestEntityManager

    private val creatorRoleName = UserRole.CREATOR.value
    private val executorRoleName = UserRole.EXECUTOR.value

    private val privilege1     = "PRIVILEGE_1"
    private val privilege2     = "PRIVILEGE_2"
    private val privilege21    = "PRIVILEGE_2.1"
    private val privilege211   = "PRIVILEGE_2.1.1"
    private val privilege212   = "PRIVILEGE_2.1.2"
    private val privilege2121  = "PRIVILEGE_2.1.2.1"
    private val privilege21211 = "PRIVILEGE_2.1.2.1.1"
    private val privilege22    = "PRIVILEGE_2.2"
    private val privilege221   = "PRIVILEGE_2.2.1"
    private val privilege222   = "PRIVILEGE_2.2.2"
    private val privilege223   = "PRIVILEGE_2.2.3"
    private val privilege3     = "PRIVILEGE_3"
    private val privilege31    = "PRIVILEGE_3.1"
    private val privilege32    = "PRIVILEGE_3.2"
    private val privilege321   = "PRIVILEGE_3.2.1"
    private val privilege322   = "PRIVILEGE_3.2.2"

    private val privilege4     = "PRIVILEGE_4"
    private val privilege41    = "PRIVILEGE_4.1"
    private val privilege42    = "PRIVILEGE_4.2"
    private val privilege421   = "PRIVILEGE_4.2.1"
    private val privilege422   = "PRIVILEGE_4.2.2"

    private val privilegesBranch1 = hashSetOf<String>()
    private val privilegesBranch2 = hashSetOf<String>()
    private val privilegesBranch3 = hashSetOf<String>()
    private val privilegesBranch4 = hashSetOf<String>()

    @BeforeEach
    fun persistEntities() {
        branch(privilegesBranch1) {
            root(privilege1)
        }

        branch(privilegesBranch2) {
            root(privilege2) {
                child(privilege21) {
                    child(privilege211)
                    child(privilege212) {
                        child(privilege2121) {
                            child(privilege21211)
                        }
                    }
                }
                child(privilege22) {
                    child(privilege221)
                    child(privilege222)
                    child(privilege223)
                }
            }
        }

        branch(privilegesBranch3) {
            root(privilege3) {
                child(privilege31)
                child(privilege32) {
                    child(privilege321)
                    child(privilege322)
                }
            }
        }

        branch(privilegesBranch4) {
            root(privilege4) {
                child(privilege41)
                child(privilege42) {
                    child(privilege421)
                    child(privilege422)
                }
            }
        }

        role(creatorRoleName) {
            privileges.add(privilegeRepository.findByName(privilege1).get())
            privileges.add(privilegeRepository.findByName(privilege2).get())
            privileges.add(privilegeRepository.findByName(privilege32).get())
            privileges.add(privilegeRepository.findByName(privilege422).get())
        }

        role(executorRoleName) {
            privileges.add(privilegeRepository.findByName(privilege22).get())
            privileges.add(privilegeRepository.findByName(privilege31).get())
            privileges.add(privilegeRepository.findByName(privilege322).get())
            privileges.add(privilegeRepository.findByName(privilege421).get())
        }
    }

    @Test
    open fun findBranchTest() {
        assertBranch(privilege1, privilegesBranch1) { assertPrivilegesBranch1(it) }
        assertBranch(privilege2, privilegesBranch2) { assertPrivilegesBranch2(it) }
        assertBranch(privilege3, privilegesBranch3) { assertPrivilegesBranch3(it) }
        assertBranch(privilege4, privilegesBranch4) { assertPrivilegesBranch4(it) }
    }

    private fun assertBranch(privilegeName: String,
                             expected: HashSet<String>,
                             assert: (HashSet<Privilege>) -> Unit
    ) {
        val privilegesBranch = privilegeRepository.findBranch(privilegeName.findId())
        Assertions.assertTrue(expected.isNotEmpty())
        Assertions.assertTrue(privilegesBranch.isNotEmpty())
        Assertions.assertEquals(expected.size, privilegesBranch.size)
        Assertions.assertTrue(privilegesBranch.names.containsAll(expected))
        assert(privilegesBranch)
    }

    @Test
    open fun findByUserRolesTest() {
        creatorRoleName.findPrivileges().also {
            val expected =
                privilegesBranch1 +
                privilegesBranch2 +
                setOf(privilege32, privilege321, privilege322) +
                setOf(privilege422)

            Assertions.assertEquals(expected.size, it.size)
            Assertions.assertTrue(it.names.containsAll(expected))
        }



        executorRoleName.findPrivileges().also {
            val expected =
                listOf(privilege22, privilege221, privilege222, privilege223) +
                listOf(privilege31) +
                listOf(privilege322) +
                listOf(privilege421)

            Assertions.assertEquals(expected.size, it.size)
            Assertions.assertTrue(it.names.containsAll(expected))
        }
    }

    private fun assertPrivilegesBranch1(privilegesBranch: HashSet<Privilege>) {
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege1).size)
    }

    private fun assertPrivilegesBranch2(privilegesBranch: HashSet<Privilege>) {
        Assertions.assertEquals(2, privilegesBranch.childrenOf(privilege2).size)
        Assertions.assertEquals(2, privilegesBranch.childrenOf(privilege21).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege211).size)
        Assertions.assertEquals(1, privilegesBranch.childrenOf(privilege212).size)
        Assertions.assertEquals(1, privilegesBranch.childrenOf(privilege2121).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege21211).size)
        Assertions.assertEquals(3, privilegesBranch.childrenOf(privilege22).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege221).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege222).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege223).size)
    }

    private fun assertPrivilegesBranch3(privilegesBranch: HashSet<Privilege>) {
        Assertions.assertEquals(2, privilegesBranch.childrenOf(privilege3).size)
        Assertions.assertEquals(2, privilegesBranch.childrenOf(privilege32).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege321).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege322).size)
    }

    private fun assertPrivilegesBranch4(privilegesBranch: HashSet<Privilege>) {
        Assertions.assertEquals(2, privilegesBranch.childrenOf(privilege4).size)
        Assertions.assertEquals(2, privilegesBranch.childrenOf(privilege42).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege421).size)
        Assertions.assertEquals(0, privilegesBranch.childrenOf(privilege422).size)
    }

    private fun HashSet<Privilege>.childrenOf(privilegeName: String) =
        filter { it.parentId == privilegeName.findId() }



    private inline fun role(roleName: String, create: Role.() -> Unit = {}): Role {
        return Role().apply {
                name = UserRole.convert(roleName)
                create()
                persist()
            }
    }

    fun AbstractEntity.persist() {
        testEntityManager.persistAndFlush(this)
    }

    private fun branch(privilegesBranch: HashSet<String>, block: PrivilegeDsl.() -> Unit) {
        PrivilegeDsl(privilegesBranch).block()
    }

    inner class PrivilegeDsl(val privilegesBranch: HashSet<String>) {
        inline fun PrivilegeDsl.root(privilegeName: String, create: Privilege.() -> Unit = {}): Privilege {
            return Privilege().apply {
                name = privilegeName
                privilegesBranch.add(name)
                persist()
                create()
            }
        }

        inline fun Privilege.child(childName: String, create: Privilege.() -> Unit = {}): Privilege {
            val parent = this
            return Privilege().apply {
                name = childName
                privilegesBranch.add(name)
                parentId = parent.id
                persist()
                create()
            }
        }
    }

    private fun String.findPrivileges(): HashSet<Privilege> = privilegeRepository.findByUserRoles(listOf(this))

    private fun String.find(): Privilege = privilegeRepository
        .findByName(this)
        .orElseThrow {
            RuntimeException("There is no ${Privilege::class.java.simpleName} with name '$this'")
        }

    private fun String.findId(): Long = find().id

    private val HashSet<Privilege>.names get() = map { it.name }
}