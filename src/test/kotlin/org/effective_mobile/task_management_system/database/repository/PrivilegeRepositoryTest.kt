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

/**
 * Tests for [PrivilegeRepository].
 */
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
            privileges.add(privilege1.find())
            privileges.add(privilege2.find())
            privileges.add(privilege32.find())
            privileges.add(privilege422.find())
        }

        role(executorRoleName) {
            privileges.add(privilege22.find())
            privileges.add(privilege31.find())
            privileges.add(privilege322.find())
            privileges.add(privilege421.find())
        }
    }

    /**
     * Test for [PrivilegeRepository.findBranch].
     */
    @Test
    open fun findBranchTest() {
        Assertions.assertEquals(1,  privilege1.findBranch().size)
        Assertions.assertEquals(10, privilege2.findBranch().size)
        Assertions.assertEquals(5,  privilege21.findBranch().size)
        Assertions.assertEquals(1,  privilege211.findBranch().size)
        Assertions.assertEquals(3,  privilege212.findBranch().size)
        Assertions.assertEquals(2,  privilege2121.findBranch().size)
        Assertions.assertEquals(1,  privilege21211.findBranch().size)
        Assertions.assertEquals(4,  privilege22.findBranch().size)
        Assertions.assertEquals(1,  privilege221.findBranch().size)
        Assertions.assertEquals(1,  privilege222.findBranch().size)
        Assertions.assertEquals(1,  privilege223.findBranch().size)
        Assertions.assertEquals(5,  privilege3.findBranch().size)
        Assertions.assertEquals(1,  privilege31.findBranch().size)
        Assertions.assertEquals(3,  privilege32.findBranch().size)
        Assertions.assertEquals(1,  privilege321.findBranch().size)
        Assertions.assertEquals(1,  privilege322.findBranch().size)
        Assertions.assertEquals(5,  privilege4.findBranch().size)
        Assertions.assertEquals(1,  privilege41.findBranch().size)
        Assertions.assertEquals(3,  privilege42.findBranch().size)
        Assertions.assertEquals(1,  privilege421.findBranch().size)
        Assertions.assertEquals(1,  privilege422.findBranch().size)
    }

    /**
     * Test for [PrivilegeRepository.findByUserRoles].
     */
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
                listOf(
                    privilege22, privilege221, privilege222, privilege223,
                    privilege31,
                    privilege322,
                    privilege421
                )

            Assertions.assertEquals(expected.size, it.size)
            Assertions.assertTrue(it.names.containsAll(expected))
        }
    }

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

    private fun String.findPrivileges() = privilegeRepository.findByUserRoles(listOf(this))
    private fun String.findBranch() = privilegeRepository.findBranch(this.findId())

    private fun String.find() =
        privilegeRepository
            .findByName(this)
            .orElseThrow {
                RuntimeException(
                    "There is no ${Privilege::class.java.simpleName} with name '$this'")
            }

    private fun String.findId(): Long = find().id

    private val HashSet<Privilege>.names get() = map { it.name }
}