package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import org.basecode.common.dict.LocalCache;
import org.basecode.common.criterion.annotations.DateRaw;
import org.basecode.common.criterion.annotations.DictField;
import org.basecode.common.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class ExtendJavaBeanSerializer extends JavaBeanSerializer {
    public ExtendJavaBeanSerializer(Class<?> beanType) {
        super(beanType);
    }

    protected void write(JSONSerializer serializer, //
                         Object object, //
                         Object fieldName, //
                         Type fieldType, //
                         int features,
                         boolean unwrapped
    ) throws IOException {
        SerializeWriter out = serializer.out;

        if (object == null) {
            out.writeNull();
            return;
        }

        if (writeReference(serializer, object, features)) {
            return;
        }

        final FieldSerializer[] getters;

        if (out.sortField) {
            getters = this.sortedGetters;
        } else {
            getters = this.getters;
        }

        SerialContext parent = serializer.context;
        if (!this.beanInfo.beanType.isEnum()) {
            serializer.setContext(parent, object, fieldName, this.beanInfo.features, features);
        }

        final boolean writeAsArray = isWriteAsArray(serializer, features);

        FieldSerializer errorFieldSerializer = null;
        try {
            final char startSeperator = writeAsArray ? '[' : '{';
            final char endSeperator = writeAsArray ? ']' : '}';
            if (!unwrapped) {
                out.append(startSeperator);
            }

            if (getters.length > 0 && out.isEnabled(SerializerFeature.PrettyFormat)) {
                serializer.incrementIndent();
                serializer.println();
            }

            boolean commaFlag = false;

            if ((this.beanInfo.features & SerializerFeature.WriteClassName.mask) != 0
                    ||(features & SerializerFeature.WriteClassName.mask) != 0
                    || serializer.isWriteClassName(fieldType, object)) {
                Class<?> objClass = object.getClass();

                final Type type;
                if (objClass != fieldType && fieldType instanceof WildcardType) {
                    type = TypeUtils.getClass(fieldType);
                } else {
                    type = fieldType;
                }

                if (objClass != type) {
                    writeClassName(serializer, beanInfo.typeKey, object);
                    commaFlag = true;
                }
            }

            char seperator = commaFlag ? ',' : '\0';

            final boolean writeClassName = out.isEnabled(SerializerFeature.WriteClassName);
            char newSeperator = this.writeBefore(serializer, object, seperator);
            commaFlag = newSeperator == ',';

            final boolean skipTransient = out.isEnabled(SerializerFeature.SkipTransientField);
            final boolean ignoreNonFieldGetter = out.isEnabled(SerializerFeature.IgnoreNonFieldGetter);

            for (int i = 0; i < getters.length; ++i) {
                FieldSerializer fieldSerializer = getters[i];

                Field field = fieldSerializer.fieldInfo.field;
                FieldInfo fieldInfo = fieldSerializer.fieldInfo;
                String fieldInfoName = fieldInfo.name;
                Class<?> fieldClass = fieldInfo.fieldClass;

                final boolean fieldUseSingleQuotes = SerializerFeature.isEnabled(out.features, fieldInfo.serialzeFeatures, SerializerFeature.UseSingleQuotes);
                final boolean directWritePrefix = out.quoteFieldNames && !fieldUseSingleQuotes;

                if (skipTransient) {
                    if (fieldInfo != null) {
                        if (fieldInfo.fieldTransient) {
                            continue;
                        }
                    }
                }

                if (ignoreNonFieldGetter) {
                    if (field == null) {
                        continue;
                    }
                }

                boolean notApply = false;
                if ((!this.applyName(serializer, object, fieldInfoName)) //
                        || !this.applyLabel(serializer, fieldInfo.label)) {
                    if (writeAsArray) {
                        notApply = true;
                    } else {
                        continue;
                    }
                }

                if (beanInfo.typeKey != null
                        && fieldInfoName.equals(beanInfo.typeKey)
                        && serializer.isWriteClassName(fieldType, object)) {
                    continue;
                }

                Object propertyValue;

                if (notApply) {
                    propertyValue = null;
                } else {
                    try {
                        propertyValue = fieldSerializer.getPropertyValueDirect(object);
                    } catch (InvocationTargetException ex) {
                        errorFieldSerializer = fieldSerializer;
                        if (out.isEnabled(SerializerFeature.IgnoreErrorGetter)) {
                            propertyValue = null;
                        } else {
                            throw ex;
                        }
                    }
                }

                if (!this.apply(serializer, object, fieldInfoName, propertyValue)) {
                    continue;
                }

                if (fieldClass == String.class && "trim".equals(fieldInfo.format)) {
                    if (propertyValue != null) {
                        propertyValue = ((String) propertyValue).trim();
                    }
                }

                String key = fieldInfoName;
                key = this.processKey(serializer, object, key, propertyValue);

                Object originalValue = propertyValue;
                propertyValue = this.processValue(serializer, fieldSerializer.fieldContext, object, fieldInfoName,
                        propertyValue, features);

                if (propertyValue == null) {
                    int serialzeFeatures = fieldInfo.serialzeFeatures;
                    JSONField jsonField = fieldInfo.getAnnotation();
                    if (beanInfo.jsonType != null) {
                        serialzeFeatures |= SerializerFeature.of(beanInfo.jsonType.serialzeFeatures());
                    }
                    // beanInfo.jsonType
                    if (jsonField != null && !"".equals(jsonField.defaultValue())) {
                        propertyValue = jsonField.defaultValue();
                    } else if (fieldClass == Boolean.class) {
                        int defaultMask = SerializerFeature.WriteNullBooleanAsFalse.mask;
                        final int mask = defaultMask | SerializerFeature.WriteMapNullValue.mask;
                        if ((!writeAsArray) && (serialzeFeatures & mask) == 0 && (out.features & mask) == 0) {
                            continue;
                        } else if ((serialzeFeatures & defaultMask) != 0) {
                            propertyValue = false;
                        } else if ((out.features & defaultMask) != 0
                                && (serialzeFeatures & SerializerFeature.WriteMapNullValue.mask) == 0) {
                            propertyValue = false;
                        }
                    } else if (fieldClass == String.class) {
                        int defaultMask = SerializerFeature.WriteNullStringAsEmpty.mask;
                        final int mask = defaultMask | SerializerFeature.WriteMapNullValue.mask;
                        if ((!writeAsArray) && (serialzeFeatures & mask) == 0 && (out.features & mask) == 0) {
                            continue;
                        } else if ((serialzeFeatures & defaultMask) != 0) {
                            propertyValue = "";
                        } else if ((out.features & defaultMask) != 0
                                && (serialzeFeatures & SerializerFeature.WriteMapNullValue.mask) == 0) {
                            propertyValue = "";
                        }
                    } else if (Number.class.isAssignableFrom(fieldClass)) {
                        int defaultMask = SerializerFeature.WriteNullNumberAsZero.mask;
                        final int mask = defaultMask | SerializerFeature.WriteMapNullValue.mask;
                        if ((!writeAsArray) && (serialzeFeatures & mask) == 0 && (out.features & mask) == 0) {
                            continue;
                        } else if ((serialzeFeatures & defaultMask) != 0) {
                            propertyValue = 0;
                        } else if ((out.features & defaultMask) != 0
                                && (serialzeFeatures & SerializerFeature.WriteMapNullValue.mask) == 0) {
                            propertyValue = 0;
                        }
                    } else if (Collection.class.isAssignableFrom(fieldClass)) {
                        int defaultMask = SerializerFeature.WriteNullListAsEmpty.mask;
                        final int mask = defaultMask | SerializerFeature.WriteMapNullValue.mask;
                        if ((!writeAsArray) && (serialzeFeatures & mask) == 0 && (out.features & mask) == 0) {
                            continue;
                        } else if ((serialzeFeatures & defaultMask) != 0) {
                            propertyValue = Collections.emptyList();
                        } else if ((out.features & defaultMask) != 0
                                && (serialzeFeatures & SerializerFeature.WriteMapNullValue.mask) == 0) {
                            propertyValue = Collections.emptyList();
                        }
                    } else if ((!writeAsArray) && (!fieldSerializer.writeNull)
                            && !out.isEnabled(SerializerFeature.WriteMapNullValue.mask)
                            && (serialzeFeatures & SerializerFeature.WriteMapNullValue.mask) == 0) {
                        continue;
                    }
                }

                if (propertyValue != null  //
                        && (out.notWriteDefaultValue //
                        || (fieldInfo.serialzeFeatures & SerializerFeature.NotWriteDefaultValue.mask) != 0 //
                        || (beanInfo.features & SerializerFeature.NotWriteDefaultValue.mask) != 0 //
                )) {
                    Class<?> fieldCLass = fieldInfo.fieldClass;
                    if (fieldCLass == byte.class && propertyValue instanceof Byte
                            && ((Byte) propertyValue).byteValue() == 0) {
                        continue;
                    } else if (fieldCLass == short.class && propertyValue instanceof Short
                            && ((Short) propertyValue).shortValue() == 0) {
                        continue;
                    } else if (fieldCLass == int.class && propertyValue instanceof Integer
                            && ((Integer) propertyValue).intValue() == 0) {
                        continue;
                    } else if (fieldCLass == long.class && propertyValue instanceof Long
                            && ((Long) propertyValue).longValue() == 0L) {
                        continue;
                    } else if (fieldCLass == float.class && propertyValue instanceof Float
                            && ((Float) propertyValue).floatValue() == 0F) {
                        continue;
                    } else if (fieldCLass == double.class && propertyValue instanceof Double
                            && ((Double) propertyValue).doubleValue() == 0D) {
                        continue;
                    } else if (fieldCLass == boolean.class && propertyValue instanceof Boolean
                            && !((Boolean) propertyValue).booleanValue()) {
                        continue;
                    }
                }

                if (commaFlag) {
                    if (fieldInfo.unwrapped
                            && propertyValue instanceof Map
                            && ((Map) propertyValue).size() == 0) {
                        continue;
                    }

                    out.write(',');
                    if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                        serializer.println();
                    }
                }

                if (key != fieldInfoName) {
                    if (!writeAsArray) {
                        out.writeFieldName(key, true);
                    }

                    serializer.write(propertyValue);
                } else if (originalValue != propertyValue) {
                    if (!writeAsArray) {
                        fieldSerializer.writePrefix(serializer);
                    }
                    serializer.write(propertyValue);
                } else {
                    if (!writeAsArray) {
                        boolean isMap = Map.class.isAssignableFrom(fieldClass);
                        boolean isJavaBean = !fieldClass.isPrimitive() && !fieldClass.getName().startsWith("java.") || fieldClass == Object.class;
                        if (writeClassName || !fieldInfo.unwrapped || !(isMap || isJavaBean)) {
                            if (directWritePrefix) {
                                out.write(fieldInfo.name_chars, 0, fieldInfo.name_chars.length);
                            } else {
                                fieldSerializer.writePrefix(serializer);
                            }
                        }
                    }

                    if (!writeAsArray) {
                        JSONField fieldAnnotation = fieldInfo.getAnnotation();
                        if (fieldClass == String.class && (fieldAnnotation == null || fieldAnnotation.serializeUsing() == Void.class)) {
                            if (propertyValue == null) {
                                int serialzeFeatures = fieldSerializer.features;
                                if (beanInfo.jsonType != null) {
                                    serialzeFeatures |= SerializerFeature.of(beanInfo.jsonType.serialzeFeatures());
                                }
                                if ((out.features & SerializerFeature.WriteNullStringAsEmpty.mask) != 0
                                        && (serialzeFeatures & SerializerFeature.WriteMapNullValue.mask) == 0) {
                                    out.writeString("");
                                } else if ((serialzeFeatures & SerializerFeature.WriteNullStringAsEmpty.mask) != 0) {
                                    out.writeString("");
                                } else {
                                    out.writeNull();
                                }
                            } else {
                                String propertyValueString = (String) propertyValue;

                                if (fieldUseSingleQuotes) {
                                    out.writeStringWithSingleQuote(propertyValueString);
                                } else {
                                    out.writeStringWithDoubleQuote(propertyValueString, (char) 0);
                                }
                            }
                        } else {
                            if (fieldInfo.unwrapped
                                    && propertyValue instanceof Map
                                    && ((Map) propertyValue).size() == 0) {
                                commaFlag = false;
                                continue;
                            }

                            fieldSerializer.writeValue(serializer, propertyValue);
                        }
                    } else {
                        fieldSerializer.writeValue(serializer, propertyValue);
                    }
                }

                boolean fieldUnwrappedNull = false;
                if (fieldInfo.unwrapped
                        && propertyValue instanceof Map) {
                    Map map = ((Map) propertyValue);
                    if (map.size() == 0) {
                        fieldUnwrappedNull = true;
                    } else if (!serializer.isEnabled(SerializerFeature.WriteMapNullValue)){
                        boolean hasNotNull = false;
                        for (Object value : map.values()) {
                            if (value != null) {
                                hasNotNull = true;
                                break;
                            }
                        }
                        if (!hasNotNull) {
                            fieldUnwrappedNull = true;
                        }
                    }
                }

                if (!fieldUnwrappedNull) {
                    commaFlag = true;
                }
                filedPorcess(serializer, key, propertyValue, fieldInfo);
            }

            this.writeAfter(serializer, object, commaFlag ? ',' : '\0');

            if (getters.length > 0 && out.isEnabled(SerializerFeature.PrettyFormat)) {
                serializer.decrementIdent();
                serializer.println();
            }

            if (!unwrapped) {
                out.append(endSeperator);
            }
        } catch (Exception e) {
            String errorMessage = "write javaBean error, fastjson version " + JSON.VERSION;
            if (object != null) {
                errorMessage += ", class " + object.getClass().getName();
            }
            if (fieldName != null) {
                errorMessage += ", fieldName : " + fieldName;
            } else if (errorFieldSerializer != null && errorFieldSerializer.fieldInfo != null) {
                FieldInfo fieldInfo = errorFieldSerializer.fieldInfo;
                if (fieldInfo.method != null) {
                    errorMessage += ", method : " + fieldInfo.method.getName();
                } else {
                    errorMessage += ", fieldName : " + errorFieldSerializer.fieldInfo.name;
                }
            }
            if (e.getMessage() != null) {
                errorMessage += (", " + e.getMessage());
            }

            Throwable cause = null;
            if (e instanceof InvocationTargetException) {
                cause = e.getCause();
            }
            if (cause == null) {
                cause = e;
            }

            throw new JSONException(errorMessage, cause);
        } finally {
            serializer.context = parent;
        }
    }

    private void filedPorcess(JSONSerializer serializer, String key, Object val, FieldInfo fieldInfo){
        SerializeWriter out = serializer.out;
        if (val instanceof String || val instanceof Integer) {
            DictField dictField = fieldInfo.field.getAnnotation(DictField.class);
            if(dictField != null && StringUtils.isNotBlank(dictField.value())) {
                try {
                    Object dictNameVal = LocalCache.nameCache.getIfPresent(dictField.value()).get(val);
                    out.append(",");
                    if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                        serializer.println();
                    }
                    out.writeFieldName(key + "Text", true);
                    out.writeString(dictNameVal.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (val instanceof Date){
            DateRaw dateRaw = fieldInfo.field.getAnnotation(DateRaw.class);
            if(dateRaw != null){
                out.append(",");
                if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                    serializer.println();
                }
                out.writeFieldName(key + "Raw", true);
                out.writeLong(((Date) val).getTime());
            }
        }
    }
}