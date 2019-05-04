/* $Id$ */
package com.muthuraj.roomcolumnfield.generator

import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * Created by Muthuraj on 2019-04-22.
 *
 * Jambav, Zoho Corporation
 */
@SupportedAnnotationTypes("androidx.room.Entity")
class RoomColumnProcessor : AbstractProcessor() {

    private val classes = HashSet<ClassData>()
    private lateinit var typeUtils: Types
    private lateinit var messager: Messager
    private lateinit var elementUtils: Elements
    private var ignoreAnnotation: TypeMirror? = null
    private var roomClassAnnotation: TypeElement? = null
    private var fileGenerator: FileGenerator? = null
    private var done = false

    override fun init(processingEnv: ProcessingEnvironment) {
        println("RoomColumnProcessor init")
        super.init(processingEnv)
        typeUtils = processingEnv.typeUtils!!
        messager = processingEnv.messager!!
        elementUtils = processingEnv.elementUtils!!

        // If the Room class isn't found something is wrong the project setup.
        // Most likely Room isn't on the class path, so just disable the
        // annotation processor
        val isRoomAvailable = elementUtils.getTypeElement("androidx.room.Room") != null
        if (!isRoomAvailable) {
            done = true
        } else {
            ignoreAnnotation = elementUtils.getTypeElement("androidx.room.Ignore")?.asType()
            roomClassAnnotation = elementUtils.getTypeElement("androidx.room.Entity")
            fileGenerator = FileGenerator(processingEnv.filer)
        }
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (done) {
            return false
        }

        // Create all proxy classes
        roundEnv.getElementsAnnotatedWith(roomClassAnnotation).forEach { classElement ->
            val classData = processClass(classElement as TypeElement)
            classes.add(classData)
        }

        done = fileGenerator!!.generate(classes)
        return false
    }

    private fun processClass(classElement: TypeElement): ClassData {
        val packageName = getPackageName(classElement)

        val entityClass = Class.forName("androidx.room.Entity") as Class<Annotation>
        val annotation = classElement.getAnnotation<Annotation>(entityClass)
        val tableNameMethod = entityClass
            .declaredMethods
            .find { it.name == "tableName" }!!
            .also { it.isAccessible = true }

        //Field helper class name would be tableName if specified through Entity annotation,
        //or simple the Entity class name.
        val tableName =
            (tableNameMethod.invoke(annotation) as String?)
                ?.takeIf { it.isNotEmpty() }
                    ?: classElement.simpleName.toString()

        val data = ClassData(packageName, tableName, classElement.simpleName.toString())

        // Find all appropriate fields
        classElement.enclosedElements.forEach {
            val elementKind = it.kind
            if (elementKind == ElementKind.FIELD) {
                val variableElement = it as VariableElement

                val modifiers = variableElement.modifiers
                if (modifiers.contains(Modifier.STATIC)) {
                    return@forEach // completely ignore any static fields
                }

                // Don't add any fields marked with @Ignore
                val ignoreField = variableElement.annotationMirrors
                    .map { it.annotationType.toString() }
                    .contains("androidx.room.Ignore")

                if (!ignoreField) {
                    data.addField(it.getSimpleName().toString(), null)
                }
            }
        }

        return data
    }

    private fun getPackageName(classElement: TypeElement): String? {
        val enclosingElement = classElement.enclosingElement

        if (enclosingElement.kind != ElementKind.PACKAGE) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Could not determine the package name. Enclosing element was: " + enclosingElement.kind
            )
            return null
        }

        val packageElement = enclosingElement as PackageElement
        return packageElement.qualifiedName.toString()
    }
}