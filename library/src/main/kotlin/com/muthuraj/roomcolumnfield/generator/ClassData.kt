/* $Id$ */
package com.muthuraj.roomcolumnfield.generator

import java.util.*

/**
 * Created by Muthuraj on 2019-04-22.
 *
 * Jambav, Zoho Corporation
 */
/**
 * Class responsible for keeping track of the metadata for each Realm model class.
 */
class ClassData(
    val packageName: String?,
    val tableClassName: String,
    val actualClassName: String,
    val libraryClass: Boolean = false
) {

    val fields = TreeMap<String, String?>() // <fieldName, linkedType or null>

    fun addField(field: String, linkedType: String?) {
        fields[field] = linkedType
    }

    val qualifiedClassName: String
        get() {
            return if (packageName?.isNotEmpty() == true) {
                "$packageName.$tableClassName"
            } else {
                tableClassName
            }
        }
}