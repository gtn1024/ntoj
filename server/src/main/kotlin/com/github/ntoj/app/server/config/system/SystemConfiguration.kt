package com.github.ntoj.app.server.config.system

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.ntoj.app.server.model.entities.System
import com.github.ntoj.app.server.model.entities.SystemValue
import com.github.ntoj.app.server.repository.SystemRepository
import com.github.ntoj.app.shared.model.LanguageStructure
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.jvm.optionals.getOrNull

typealias LanguageMap = Map<String, LanguageStructure>

@Configuration
class SystemConfiguration(
    val systemRepository: SystemRepository,
) {
    companion object {
        val yamlObjectMapper: ObjectMapper =
            ObjectMapper(YAMLFactory()).apply {
                findAndRegisterModules()
                registerModules(KotlinModule.Builder().build())
            }
    }

    @Bean
    fun languages(): LanguageMap {
        val defaultLanguages =
            """
            bash:
              display: Bash
              source: foo.sh
              execute: /bin/bash foo.sh
              highlight: bash
            cat:
              display: 文本输出
              source: foo
              execute: /bin/cat foo
            c99:
              compile: /usr/bin/gcc -Wall --std=c99 -o foo foo.c -lm
              source: foo.c
              target: foo
              execute: /w/foo
              highlight: c
              editor: c
              display: C99
            cc98:
              compile: /usr/bin/g++ -Wall -std=c++98 -o foo foo.cc -lm -O2 -I/include
              source: foo.cc
              target: foo
              execute: /w/foo
              highlight: cpp
              editor: cpp
              display: C++98
            cc11:
              compile: /usr/bin/g++ -Wall -std=c++11 -o foo foo.cc -lm -O2 -I/include
              source: foo.cc
              target: foo
              execute: /w/foo
              highlight: cpp
              editor: cpp
              display: C++11
            cc14:
              compile: /usr/bin/g++ -Wall -std=c++14 -o foo foo.cc -lm -O2 -I/include
              source: foo.cc
              target: foo
              execute: /w/foo
              highlight: cpp
              editor: cpp
              display: C++14
            cc17:
              compile: /usr/bin/g++ -Wall -std=c++17 -o foo foo.cc -lm -O2 -I/include
              source: foo.cc
              target: foo
              execute: /w/foo
              highlight: cpp
              editor: cpp
              display: C++17
            pas:
              compile: /usr/bin/fpc -O2 -o/w/foo foo.pas
              source: foo.pas
              target: foo
              execute: /w/foo
              highlight: pascal
              editor: pascal
              display: Pascal
            java:
              compile: javac -d /w -encoding utf8 ./Main.java
              source: Main.java
              target: Main.class
              execute: /usr/bin/java Main
              timeLimitRate: 2
              memoryLimitRate: 2
              highlight: java
              display: Java
              editor: java
              compileTimeLimit: 30000
            kt:
              compile: kotlinc -d /w ./Main.kt
              source: Main.kt
              target: MainKt.class
              execute: kotlin MainKt
              timeLimitRate: 2
              memoryLimitRate: 2
              highlight: kotlin
              display: Kotlin
              editor: kotlin
              compileTimeLimit: 30000
            py3:
              compile: /usr/bin/python3 -c "import py_compile; py_compile.compile('main.py', 'main', doraise=True)"
              source: main.py
              target: main
              execute: /usr/bin/python3 main
              highlight: python
              editor: python
              display: Python
            go:
              compile: env GOPATH=/w GOCACHE=/tmp/ /usr/bin/go build -o foo foo.go
              execute: /w/foo
              source: foo.go
              target: foo
              highlight: go
              editor: go
              display: Golang
            """.trimIndent()
        val key = "ntoj.lang"
        var system = systemRepository.findById(key).getOrNull()
        if (system == null) {
            system = System(key, SystemValue(defaultLanguages))
            systemRepository.save(system)
        }
        val languagesYaml = system.value.value as String
        val languages = yamlObjectMapper.readValue<LanguageMap>(languagesYaml)
        return languages
    }
}
