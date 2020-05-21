/*
 *  Copyright 2011 The MyBatis Team
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
package org.basecode.core.generator.mybatis.generator.api.dom;

import org.basecode.core.generator.mybatis.generator.api.JavaFormatter;
import org.basecode.core.generator.mybatis.generator.api.dom.java.CompilationUnit;
import org.basecode.core.generator.mybatis.generator.config.Context;

/**
 * This class is the default formatter for generated Java.  This class will use the
 * built in formatting of the DOM classes directly.
 * 
 * @author Jeff Butler
 *
 */
public class DefaultJavaFormatter implements JavaFormatter {
    protected Context context;
    
    public String getFormattedContent(CompilationUnit compilationUnit) {
        return compilationUnit.getFormattedContent();
    }

    public void setContext(Context context) {
        this.context = context;
    }
}