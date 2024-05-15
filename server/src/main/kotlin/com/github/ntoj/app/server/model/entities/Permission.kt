package com.github.ntoj.app.server.model.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.math.BigInteger

@Entity(name = "permission_role")
class PermissionRole(
    @Id @Column(nullable = false) var name: String,
    @Column(nullable = false, columnDefinition = "varchar(1023)") var permission: BigInteger,
)

val BIG_1: BigInteger = BigInteger.ONE

/*
status:

PERM_VIEW                    √
PERM_EDIT_SYSTEM
PERM_SET_PERM                √
PERM_USER_PROFILE            √
PERM_REGISTER_USER           √
PERM_JUDGE                   √
 */
val Permission: Map<String, BigInteger> =
    mapOf(
        // general
        "PERM_VIEW" to (BIG_1 shl 0),
        "PERM_EDIT_SYSTEM" to (BIG_1 shl 1),
        "PERM_SET_PERM" to (BIG_1 shl 2),
        "PERM_USER_PROFILE" to (BIG_1 shl 3),
        "PERM_REGISTER_USER" to (BIG_1 shl 4),
        "PERM_JUDGE" to (BIG_1 shl 5),
        "PERM_EDIT_ANNOUNCEMENT" to (BIG_1 shl 6),
        // problem
        "PERM_PROBLEM_CREATE" to (BIG_1 shl 10),
        "PERM_EDIT_ALL_PROBLEMS" to (BIG_1 shl 11),
        "PERM_EDIT_OWN_PROBLEMS" to (BIG_1 shl 12),
        "PERM_SUBMIT_PROBLEM" to (BIG_1 shl 13),
        "PERM_VIEW_PROBLEMS" to (BIG_1 shl 14),
        "PERM_VIEW_HIDDEN_PROBLEMS" to (BIG_1 shl 15),
        // record
        "PERM_REJUDGE_RECORD" to (BIG_1 shl 20),
        // * TODO: add rejudge problem
        "PERM_REJUDGE_PROBLEM" to (BIG_1 shl 21),
        // article
        "PERM_CREATE_ARTICLE" to (BIG_1 shl 30),
        "PERM_EDIT_ALL_ARTICLES" to (BIG_1 shl 31),
        "PERM_EDIT_OWN_ARTICLES" to (BIG_1 shl 32),
        "PERM_VIEW_ARTICLE" to (BIG_1 shl 33),
        // contest
        "PERM_CREATE_CONTEST" to (BIG_1 shl 40),
        "PERM_EDIT_ALL_CONTESTS" to (BIG_1 shl 41),
        "PERM_EDIT_OWN_CONTESTS" to (BIG_1 shl 42),
        "PERM_ATTEND_CONTEST" to (BIG_1 shl 43),
        // homework
        "PERM_CREATE_HOMEWORK" to (BIG_1 shl 50),
        "PERM_EDIT_HOMEWORK" to (BIG_1 shl 51),
        "PERM_EDIT_OWN_HOMEWORK" to (BIG_1 shl 52),
    )

val PERM_GUEST = Permission["PERM_VIEW"]!!

val PERM_DEFAULT =
    Permission["PERM_VIEW"]!! or
        Permission["PERM_USER_PROFILE"]!! or
        Permission["PERM_EDIT_OWN_PROBLEMS"]!! or
        Permission["PERM_SUBMIT_PROBLEM"]!! or
        Permission["PERM_VIEW_PROBLEMS"]!! or
        Permission["PERM_CREATE_ARTICLE"]!! or
        Permission["PERM_EDIT_OWN_ARTICLES"]!! or
        Permission["PERM_VIEW_ARTICLE"]!! or
        Permission["PERM_EDIT_OWN_CONTESTS"]!! or
        Permission["PERM_ATTEND_CONTEST"]!!

val PERM_ROOT = BigInteger("-1")
