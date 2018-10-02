/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.jdbc.meta;

import java.util.Properties;

import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.schema.ForeignKey;
import org.apache.openjpa.jdbc.schema.Table;
import org.apache.openjpa.lib.util.Closeable;

/**
 * Plugin in interface to customize the output of the
 * {@link ReverseMappingTool}.
 *
 * @author Abe White
 */
public interface ReverseCustomizer
    extends Closeable {

    /**
     * Set configuration properties given by the user.
     */
    public void setConfiguration(Properties props);

    /**
     * Set the reverse mapping tool using this customizer. You can use
     * the tool to see how it is configured, or to use utility methods the
     * tool provides.
     */
    public void setTool(ReverseMappingTool tool);

    /**
     * Return the type of the given table, or the given default type.
     * See the TABLE_XXX constants in {@link ReverseMappingTool}.
     */
    public int getTableType(Table table, int defaultType);

    /**
     * Return the fully-qualified class name to generate for the given table.
     * Return null to prevent the table from being mapped. Return the given
     * default name if it is acceptable.
     */
    public String getClassName(Table table, String defaultName);

    /**
     * Customize the given class information produced by the reverse mapping
     * tool. To change the application identity class, use
     * {@link ReverseMappingTool#generateClass} to creat the new class object.
     * The class will not have any fields at the time of this call.
     */
    public void customize(ClassMapping cls);

    /**
     * Return a code template for the given class, or null to use the standard
     * system-generated Java code. To facilitate template reuse, the
     * following parameters can appear in your template; the proper values
     * will be subtituted by the system:
     * <ul>
     * <li>${packageDec}: The package declaration, in the form
     * "package &lt;package name &gt;;", or empty string if no package.</li>
     * <li>${imports}: Imports for the packages used by the declared
     * field types.</li>
     * <li>${className}: The name of the class, without package.</li>
     * <li>${extendsDec}: Extends declaration, in the form
     * "extends &lt;superclass&gt;", or empty string if no superclass.</li>
     * <li>${constructor}: A constructor that takes in all primary key fields
     * of the class, or empty string if the class uses datastore identity.</li>
     * <li>${fieldDecs}: Declarations of all the generated fields.</li>
     * <li>${fieldCode}: Get/set methods for all the generated fields.</li>
     * </ul>
     */
    public String getClassCode(ClassMapping mapping);

    /**
     * Return the field name used to map the given columns, or null to prevent
     * the columns from being mapped. Return the given default if it is
     * acceptable.
     *
     * @param dec the class that will declare this field
     * @param cols the column(s) this field will represent
     * @param fk for relation fields, the foreign key to the related type
     */
    public String getFieldName(ClassMapping dec, Column[] cols, ForeignKey fk,
        String defaultName);

    /**
     * Customize the given field information produced by the reverse mapping
     * tool.
     */
    public void customize(FieldMapping field);

    /**
     * Return code for the initial value for the given field, or null to use
     * the default generated by the system.
     */
    public String getInitialValue(FieldMapping field);

    /**
     * Return a code template for the declaration of the given field, or null
     * to use the system-generated default Java code.
     * To facilitate template reuse, the following parameters can appear in
     * your template; the proper values will be subtituted by the system:
     * <ul>
     * <li>${fieldName}: The name of the field.</li>
     * <li>${capFieldName}: The capitalized field name.</li>
     * <li>${propertyName}: The field name without leading '_', if any.</li>
     * <li>${fieldType}: The field's type name.</li>
     * <li>${fieldValue}: The field's initial value, in the form
     * " = &lt;value&gt;", or empty string if none.</li>
     * </ul>
     */
    public String getDeclaration(FieldMapping field);

    /**
     * Return a code template for the get/set methods of the given field, or
     * null to use the system-generated default Java code.
     * To facilitate template reuse, the following parameters can appear in
     * your template; the proper values will be subtituted by the system:
     * <ul>
     * <li>${fieldName}: The name of the field.</li>
     * <li>${capFieldName}: The capitalized field name.</li>
     * <li>${propertyName}: The field name without leading '_', if any.</li>
     * <li>${fieldType}: The field's type name.</li>
     * <li>${fieldValue}: The field's initial value, in the form
     * "= &lt;value&gt;", or empty string if none.</li>
     * </ul>
     */
    public String getFieldCode(FieldMapping field);

    /**
     * Notification that a table has gone unmapped. You can map the table
     * yourself using this method. When mapping, use
     * {@link ReverseMappingTool#generateClass} to create the class,
     * {@link ReverseMappingTool#newClassMapping} to create the class metadata,
     * and then {@link ClassMapping#addDeclaredFieldMapping} to add field
     * metadata.
     *
     * @return true if you map the table, false otherwise
     */
    public boolean unmappedTable(Table table);

    /**
     * Invoked when the customizer is no longer needed.
     */
    public void close();
}
