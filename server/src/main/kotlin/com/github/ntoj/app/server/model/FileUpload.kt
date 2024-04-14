package com.github.ntoj.app.server.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "t_files")
class FileUpload(
    var filename: String,
    var path: String,
    var hash: String,
    @Column(columnDefinition = "TEXT") var url: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    var fileId: Long? = null,
) : BaseEntity()
