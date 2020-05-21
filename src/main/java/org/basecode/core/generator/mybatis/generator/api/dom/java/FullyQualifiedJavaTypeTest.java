/*
 *  Copyright 2009 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.basecode.core.generator.mybatis.generator.api.dom.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import org.basecode.core.generator.mybatis.generator.api.dom.OutputUtilities;

/**
 * @author Jeff Butler
 *
 */
public class FullyQualifiedJavaTypeTest {

    @Test
    public void testJavaType() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.lang.String"); //$NON-NLS-1$
        assertFalse(fqjt.isExplicitlyImported());
        assertEquals("String", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.lang.String", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.lang", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(0, fqjt.getImportList().size());
    }

    @Test
    public void testSimpleType() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("com.foo.Bar"); //$NON-NLS-1$
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Bar", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("com.foo.Bar", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("com.foo", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(1, fqjt.getImportList().size());
        assertEquals("com.foo.Bar", fqjt.getImportList().get(0));
    }

    @Test
    public void testSimpleType2() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("com.foo.bar"); //$NON-NLS-1$
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("bar", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("com.foo.bar", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("com.foo", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(1, fqjt.getImportList().size());
        assertEquals("com.foo.bar", fqjt.getImportList().get(0));
    }
    
    @Test
    public void testSimpleType3() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("int"); //$NON-NLS-1$
        assertFalse(fqjt.isExplicitlyImported());
        assertEquals("int", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("int", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(0, fqjt.getImportList().size());
    }
    
    @Test
    public void testGenericType1() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.List<java.lang.String>"); //$NON-NLS-1$
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("List<String>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.List<java.lang.String>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(1, fqjt.getImportList().size());
        assertEquals("java.utils.List", fqjt.getImportList().get(0));
    }


    @Test
    public void testGenericType2() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.Map<java.lang.String, java.utils.List<java.lang.String>>"); //$NON-NLS-1$
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Map<String, List<String>>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.Map<java.lang.String, java.utils.List<java.lang.String>>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(2, fqjt.getImportList().size());
    }


    @Test
    public void testGenericType3() {
        FullyQualifiedJavaType listOfStrings = new FullyQualifiedJavaType("java.utils.List"); //$NON-NLS-1$
        listOfStrings.addTypeArgument(new FullyQualifiedJavaType("java.lang.String")); //$NON-NLS-1$

        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.Map"); //$NON-NLS-1$
        fqjt.addTypeArgument(new FullyQualifiedJavaType("java.lang.String")); //$NON-NLS-1$
        fqjt.addTypeArgument(listOfStrings);

        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Map<String, List<String>>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.Map<java.lang.String, java.utils.List<java.lang.String>>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(2, fqjt.getImportList().size());
    }

    @Test
    public void testGenericType4() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.List<java.utils.Map<java.lang.String, java.lang.Object>>"); //$NON-NLS-1$
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("List<Map<String, Object>>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.List<java.utils.Map<java.lang.String, java.lang.Object>>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(2, fqjt.getImportList().size());
    }

    @Test
    public void testWildcardType1() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.Map<java.lang.String, ? extends com.foo.Bar>");
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Map<String, ? extends Bar>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.Map<java.lang.String, ? extends com.foo.Bar>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(2, fqjt.getImportList().size());
    }

    @Test
    public void testWildcardType2() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.Map<java.lang.String, ?>");
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Map<String, ?>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.Map<java.lang.String, ?>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(1, fqjt.getImportList().size());
        assertEquals("java.utils.Map", fqjt.getImportList().get(0));
    }

    @Test
    public void testWildcardType3() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.Map<? extends java.utils.List<?>, ?>");
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Map<? extends List<?>, ?>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.Map<? extends java.utils.List<?>, ?>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(2, fqjt.getImportList().size());
    }

    @Test
    public void testWildcardType4() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.Map<?, ?>");
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Map<?, ?>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.Map<?, ?>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(1, fqjt.getImportList().size());
        assertEquals("java.utils.Map", fqjt.getImportList().get(0));
    }

    @Test
    public void testWildcardType5() {
        FullyQualifiedJavaType fqjt =
            new FullyQualifiedJavaType("java.utils.List<? extends java.utils.Map<? super java.lang.Object, ?>>");
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("List<? extends Map<? super Object, ?>>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.List<? extends java.utils.Map<? super java.lang.Object, ?>>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(2, fqjt.getImportList().size());
    }

    @Test
    public void testUppercasePackage1() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("org.foo.Bar.Inner");
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Inner", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("org.foo.Bar.Inner", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("org.foo.Bar", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(1, fqjt.getImportList().size());
        assertEquals("org.foo.Bar.Inner", fqjt.getImportList().get(0));
    }

    @Test
    public void testUppercasePackage2() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("org.foo.Bar.Inner.Inner");
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("Inner", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("org.foo.Bar.Inner.Inner", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("org.foo.Bar.Inner", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(1, fqjt.getImportList().size());
        assertEquals("org.foo.Bar.Inner.Inner", fqjt.getImportList().get(0));
    }

    @Test
    public void testUppercasePackage3() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.utils.List<org.foo.Bar.Inner>");
        assertTrue(fqjt.isExplicitlyImported());
        assertEquals("List<Inner>", fqjt.getShortName()); //$NON-NLS-1$
        assertEquals("java.utils.List<org.foo.Bar.Inner>", fqjt.getFullyQualifiedName()); //$NON-NLS-1$
        assertEquals("java.utils", fqjt.getPackageName()); //$NON-NLS-1$
        assertEquals(2, fqjt.getImportList().size());
        assertTrue(fqjt.getImportList().contains("java.utils.List"));
        assertTrue(fqjt.getImportList().contains("org.foo.Bar.Inner"));
    }
    
    @Test
    public void testImportList() {
        Set<FullyQualifiedJavaType> types = new TreeSet<FullyQualifiedJavaType>();
        
        types.add(new FullyQualifiedJavaType("foo.bar.Example"));
        types.add(new FullyQualifiedJavaType("foo.bar.Example.Criteria"));
        types.add(new FullyQualifiedJavaType("foo.bar.Example.Criterion"));
        assertEquals(3, types.size());
        
        Set<String> imports = OutputUtilities.calculateImports(types);
        assertEquals(3, imports.size());
    }
    
    @Test
    public void testByteArray1() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("byte[]");
        assertFalse(fqjt.isPrimitive());
        assertTrue(fqjt.isArray());
    }

    @Test
    public void testByteArray2() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("byte[ ]");
        assertFalse(fqjt.isPrimitive());
        assertTrue(fqjt.isArray());
    }

    @Test
    public void testStringArray() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.lang.String[]");
        assertFalse(fqjt.isPrimitive());
        assertTrue(fqjt.isArray());
    }

    @Test
    public void testComplexArray() {
        FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType("java.utils.List<String>[]");
        assertFalse(fqjt.isPrimitive());
        assertTrue(fqjt.isArray());
    }
}
