/* $Id$ */
package com.muthuraj.roomcolumnfield.generator

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.Filer

/**
 * Created by Muthuraj on 2019-04-22.
 *
 * Class responsible for creating the final output files.
 */
class FileGenerator(private val filer: Filer) {
    private val formatter = FieldNameFormatter()

    /**
     * Generates all the "&lt;class&gt;Fields" fields with field name references.
     * @param fileData Files to create.
     * *
     * @return `true` if the files where generated, `false` if not.
     */
    fun generate(fileData: Set<ClassData>): Boolean {
        return fileData
            .filter { !it.libraryClass }
            .all { generateFile(it, fileData) }
    }

    private fun generateFile(classData: ClassData, classPool: Set<ClassData>): Boolean {

        val typeSpec =
            TypeSpec.objectBuilder(classData.tableClassName + "Fields")
                .addKdoc(
                    "This class enumerate all queryable fields in {@link %L.%L}\n",
                    classData.packageName!!, classData.actualClassName
                )


        // Add a static field reference to each queryable field in the Realm model class
        classData.fields.forEach { (fieldName, value) ->
            if (value != null) {
                // Add linked field names (only up to depth 1)
                for (data in classPool) {
                    if (data.qualifiedClassName == value) {
                        val linkedTypeSpec =
                            TypeSpec.classBuilder(formatter.format(fieldName))
                                .addModifiers(KModifier.CONST)
                        val linkedClassFields = data.fields
                        addField(linkedTypeSpec, "$", fieldName)
                        for (linkedFieldName in linkedClassFields.keys) {
                            addField(
                                linkedTypeSpec,
                                linkedFieldName,
                                "$fieldName.$linkedFieldName"
                            )
                        }
                        typeSpec.addType(linkedTypeSpec.build())
                    }
                }
            } else {
                // Add normal field name
                addField(typeSpec, fieldName, fieldName)
            }
        }

        val kotlinFile =
            FileSpec.builder(classData.packageName, classData.tableClassName + "Fields")
                .addType(typeSpec.build())
                .build()
        return try {
            kotlinFile.writeTo(filer)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }

    }

    private fun addField(
        fileBuilder: TypeSpec.Builder,
        fieldName: String,
        fieldNameValue: String
    ) {
        val property =
            PropertySpec.builder(formatter.format(fieldName), String::class, KModifier.CONST)
                .initializer("%S", fieldNameValue)
                .build()
        fileBuilder.addProperty(property)
    }
}