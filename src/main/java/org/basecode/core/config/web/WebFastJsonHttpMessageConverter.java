package org.basecode.core.config.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONPObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonContainer;
import com.alibaba.fastjson.support.spring.MappingFastJsonValue;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.basecode.core.criterion.model.ResultMsg;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.TypeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Fastjson for Spring MVC Converter.
 * <p>
 * Compatible Spring MVC version 3.2+
 *
 * @author VictorZeng
 * @see AbstractHttpMessageConverter
 * @see GenericHttpMessageConverter
 * @since 1.2.10
 * <p>
 * <p>
 * <p>
 * Supported return type:
 * </p>
 * Simple object: Object
 * <p>
 * <p>
 * With property filter :FastJsonContainer[Object]
 * </p>
 * <p>
 * Jsonp :MappingFastJsonValue[Object]
 * </p>
 * Jsonp with property filter: MappingFastJsonValue[FastJsonContainer[Object]]
 */

public class WebFastJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object>//
        implements GenericHttpMessageConverter<Object> {

    public static final MediaType APPLICATION_JAVASCRIPT = new MediaType("application", "javascript");

    protected ObjectMapper objectMapper;

    @Nullable
    private Boolean prettyPrint;

    @Nullable
    private PrettyPrinter ssePrettyPrinter;

    @Nullable
    private String jsonPrefix;

    @Deprecated
    protected SerializerFeature[] features = new SerializerFeature[0];

    @Deprecated
    protected SerializeFilter[] filters = new SerializeFilter[0];

    @Deprecated
    protected String dateFormat;

    /**
     * with fastJson config
     */
    private FastJsonConfig fastJsonConfig = new FastJsonConfig();

    /**
     * @return the fastJsonConfig.
     * @since 1.2.11
     */
    public FastJsonConfig getFastJsonConfig() {
        return fastJsonConfig;
    }

    /**
     * @param fastJsonConfig the fastJsonConfig to set.
     * @since 1.2.11
     */
    public void setFastJsonConfig(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
    }

    /**
     * Can serialize/deserialize all types.
     */
    public WebFastJsonHttpMessageConverter() {
        super(MediaType.ALL);
        this.objectMapper = new ObjectMapper();

        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        this.ssePrettyPrinter = prettyPrinter;
    }

    /**
     * Gets charset.
     *
     * @return the charset
     * @see FastJsonConfig#getCharset()
     * @deprecated
     */
    @Deprecated
    public Charset getCharset() {
        return this.fastJsonConfig.getCharset();
    }

    /**
     * Sets charset.
     *
     * @param charset the charset
     * @see FastJsonConfig#setCharset(Charset)
     * @deprecated
     */
    @Deprecated
    public void setCharset(Charset charset) {
        this.fastJsonConfig.setCharset(charset);
    }

    /**
     * Gets date format.
     *
     * @return the date format
     * @see FastJsonConfig#getDateFormat()
     * @deprecated
     */
    @Deprecated
    public String getDateFormat() {
        return this.fastJsonConfig.getDateFormat();
    }

    /**
     * Sets date format.
     *
     * @param dateFormat the date format
     * @see FastJsonConfig#setDateFormat(String)
     * @deprecated
     */
    @Deprecated
    public void setDateFormat(String dateFormat) {
        this.fastJsonConfig.setDateFormat(dateFormat);
    }

    /**
     * Get features serializer feature [].
     *
     * @return the serializer feature []
     * @see FastJsonConfig#getSerializerFeatures()
     * @deprecated
     */
    @Deprecated
    public SerializerFeature[] getFeatures() {
        return this.fastJsonConfig.getSerializerFeatures();
    }

    /**
     * Sets features.
     *
     * @param features the features
     * @see FastJsonConfig#setSerializerFeatures(SerializerFeature...)
     * @deprecated
     */
    @Deprecated
    public void setFeatures(SerializerFeature... features) {
        this.fastJsonConfig.setSerializerFeatures(features);
    }

    /**
     * Get filters serialize filter [].
     *
     * @return the serialize filter []
     * @see FastJsonConfig#getSerializeFilters()
     * @deprecated
     */
    @Deprecated
    public SerializeFilter[] getFilters() {
        return this.fastJsonConfig.getSerializeFilters();
    }

    /**
     * Sets filters.
     *
     * @param filters the filters
     * @see FastJsonConfig#setSerializeFilters(SerializeFilter...)
     * @deprecated
     */
    @Deprecated
    public void setFilters(SerializeFilter... filters) {
        this.fastJsonConfig.setSerializeFilters(filters);
    }

    /**
     * Add serialize filter.
     *
     * @param filter the filter
     * @see FastJsonConfig#setSerializeFilters(SerializeFilter...)
     * @deprecated
     */
    @Deprecated
    public void addSerializeFilter(SerializeFilter filter) {
        if (filter == null) {
            return;
        }

        int length = this.fastJsonConfig.getSerializeFilters().length;
        SerializeFilter[] filters = new SerializeFilter[length + 1];
        System.arraycopy(this.fastJsonConfig.getSerializeFilters(), 0, filters, 0, length);
        filters[filters.length - 1] = filter;
        this.fastJsonConfig.setSerializeFilters(filters);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }


    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return super.canRead(contextClass, mediaType);
    }


    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return super.canWrite(clazz, mediaType);
    }

    /*
     * @see org.springframework.http.converter.GenericHttpMessageConverter#read(java.lang.reflect.Type, java.lang.Class, org.springframework.http.HttpInputMessage)
     */
    public Object read(Type type, //
                       Class<?> contextClass, //
                       HttpInputMessage inputMessage //
    ) throws IOException, HttpMessageNotReadableException {
        return readType(getType(type, contextClass), inputMessage);
    }

    /*
     * @see org.springframework.http.converter.GenericHttpMessageConverter.write
     */
    public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final HttpHeaders headers = outputMessage.getHeaders();
        addDefaultHeaders(headers, o, contentType);
//        if(o.getClass().getName().startsWith("org.springframework.http.ResponseEntity")){
        if(outputMessage.getHeaders().containsKey("Oauth2-End-Points") || outputMessage.getHeaders().containsKey("jackson")){
            writeInternal(o, type, outputMessage); //采用jackson序列化
        }else {

            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                parameterizedType.getRawType();
            }
            if (outputMessage instanceof StreamingHttpOutputMessage) {
                StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
                streamingOutputMessage.setBody(outputStream -> writeInternal(o, new HttpOutputMessage() {
                    @Override
                    public OutputStream getBody() {
                        return outputStream;
                    }

                    @Override
                    public HttpHeaders getHeaders() {
                        return headers;
                    }
                }));
            } else {
                writeInternal(o, outputMessage);
                outputMessage.getBody().flush();
            }
        }
//        super.write(o, contentType, outputMessage);// support StreamingHttpOutputMessage in spring4.0+
        //writeInternal(o, outputMessage);
    }


    /*
     * @see org.springframework.http.converter.AbstractHttpMessageConverter#readInternal(java.lang.Class, org.springframework.http.HttpInputMessage)
     */
    @Override
    protected Object readInternal(Class<? extends Object> clazz, //
                                  HttpInputMessage inputMessage //
    ) throws IOException, HttpMessageNotReadableException {
        return readType(getType(clazz, null), inputMessage);
    }

    private Object readType(Type type, HttpInputMessage inputMessage) {

        try {
            InputStream in = inputMessage.getBody();
            return JSON.parseObject(in,
                    fastJsonConfig.getCharset(),
                    type,
                    fastJsonConfig.getParserConfig(),
                    fastJsonConfig.getParseProcess(),
                    JSON.DEFAULT_PARSER_FEATURE,
                    fastJsonConfig.getFeatures());
        } catch (JSONException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("I/O error while reading input message", ex);
        }
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        ByteArrayOutputStream outnew = new ByteArrayOutputStream();
        try {
            HttpHeaders headers = outputMessage.getHeaders();

            //获取全局配置的filter
            SerializeFilter[] globalFilters = fastJsonConfig.getSerializeFilters();
            List<SerializeFilter> allFilters = new ArrayList<SerializeFilter>(Arrays.asList(globalFilters));

            boolean isJsonp = false;

            //不知道为什么会有这行代码， 但是为了保持和原来的行为一致，还是保留下来
            Object value = strangeCodeForJackson(object);

            if (value instanceof FastJsonContainer) {
                FastJsonContainer fastJsonContainer = (FastJsonContainer) value;
                PropertyPreFilters filters = fastJsonContainer.getFilters();
                allFilters.addAll(filters.getFilters());
                value = fastJsonContainer.getValue();
            }
            if(value instanceof ResultMsg){
                ResultMsg val = (ResultMsg) value;
                if(val.getFilter()!=null){
                    allFilters.add(val.getFilter());
                }
            }

            //revise 2017-10-23 ,
            // 保持原有的MappingFastJsonValue对象的contentType不做修改 保持旧版兼容。
            // 但是新的JSONPObject将返回标准的contentType：application/javascript ，不对是否有function进行判断
            if (value instanceof MappingFastJsonValue) {
                if (!StringUtils.isEmpty(((MappingFastJsonValue) value).getJsonpFunction())) {
                    isJsonp = true;
                }
            } else if (value instanceof JSONPObject) {
                isJsonp = true;
            }


            int len = JSON.writeJSONString(outnew, //
                    fastJsonConfig.getCharset(), //
                    value, //
                    fastJsonConfig.getSerializeConfig(), //
                    //fastJsonConfig.getSerializeFilters(), //
                    allFilters.toArray(new SerializeFilter[allFilters.size()]),
                    fastJsonConfig.getDateFormat(), //
                    JSON.DEFAULT_GENERATE_FEATURE, //
                    fastJsonConfig.getSerializerFeatures());

            if (isJsonp) {
                headers.setContentType(APPLICATION_JAVASCRIPT);
            }

            if (fastJsonConfig.isWriteContentLength()) {
                headers.setContentLength(len);
            }

            outnew.writeTo(outputMessage.getBody());

        } catch (JSONException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        } finally {
            outnew.close();
        }
    }

    private Object strangeCodeForJackson(Object obj) {
        if (obj != null) {
            String className = obj.getClass().getName();
            if ("com.fasterxml.jackson.databind.node.ObjectNode".equals(className)) {
                return obj.toString();
            }
        }
        return obj;
    }

    protected Type getType(Type type, Class<?> contextClass) {
        if (Spring4TypeResolvableHelper.isSupport()) {
            return Spring4TypeResolvableHelper.getType(type, contextClass);
        }

        return type;
    }


    private static class Spring4TypeResolvableHelper {
        private static boolean hasClazzResolvableType;

        static {
            try {
                Class.forName("org.springframework.core.ResolvableType");
                hasClazzResolvableType = true;
            } catch (ClassNotFoundException e) {
                hasClazzResolvableType = false;
            }
        }

        private static boolean isSupport() {
            return hasClazzResolvableType;
        }


        private static Type getType(Type type, Class<?> contextClass) {
            if (contextClass != null) {
                ResolvableType resolvedType = ResolvableType.forType(type);
                if (type instanceof TypeVariable) {
                    ResolvableType resolvedTypeVariable = resolveVariable((TypeVariable) type, ResolvableType.forClass(contextClass));
                    if (resolvedTypeVariable != ResolvableType.NONE) {
                        return resolvedTypeVariable.resolve();
                    }
                } else if (type instanceof ParameterizedType && resolvedType.hasUnresolvableGenerics()) {
                    ParameterizedType parameterizedType = (ParameterizedType) type;
                    Class<?>[] generics = new Class[parameterizedType.getActualTypeArguments().length];
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();

                    for (int i = 0; i < typeArguments.length; ++i) {
                        Type typeArgument = typeArguments[i];
                        if (typeArgument instanceof TypeVariable) {
                            ResolvableType resolvedTypeArgument = resolveVariable((TypeVariable) typeArgument, ResolvableType.forClass(contextClass));
                            if (resolvedTypeArgument != ResolvableType.NONE) {
                                generics[i] = resolvedTypeArgument.resolve();
                            } else {
                                generics[i] = ResolvableType.forType(typeArgument).resolve();
                            }
                        } else {
                            generics[i] = ResolvableType.forType(typeArgument).resolve();
                        }
                    }

                    return ResolvableType.forClassWithGenerics(resolvedType.getRawClass(), generics).getType();
                }
            }

            return type;
        }

        private static ResolvableType resolveVariable(TypeVariable<?> typeVariable, ResolvableType contextType) {
            ResolvableType resolvedType;
            if (contextType.hasGenerics()) {
                resolvedType = ResolvableType.forType(typeVariable, contextType);
                if (resolvedType.resolve() != null) {
                    return resolvedType;
                }
            }

            ResolvableType superType = contextType.getSuperType();
            if (superType != ResolvableType.NONE) {
                resolvedType = resolveVariable(typeVariable, superType);
                if (resolvedType.resolve() != null) {
                    return resolvedType;
                }
            }
            for (ResolvableType ifc : contextType.getInterfaces()) {
                resolvedType = resolveVariable(typeVariable, ifc);
                if (resolvedType.resolve() != null) {
                    return resolvedType;
                }
            }
            return ResolvableType.NONE;
        }
    }


    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = getJsonEncoding(contentType);
        JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
        try {
            writePrefix(generator, object);

            Object value = object;
            Class<?> serializationView = null;
            FilterProvider filters = null;
            JavaType javaType = null;

            if (object instanceof MappingJacksonValue) {
                MappingJacksonValue container = (MappingJacksonValue) object;
                value = container.getValue();
                serializationView = container.getSerializationView();
                filters = container.getFilters();
            }
            if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
                javaType = getJavaType(type, null);
            }

            ObjectWriter objectWriter = (serializationView != null ?
                    this.objectMapper.writerWithView(serializationView) : this.objectMapper.writer());
            if (filters != null) {
                objectWriter = objectWriter.with(filters);
            }
            if (javaType != null && javaType.isContainerType()) {
                objectWriter = objectWriter.forType(javaType);
            }
            SerializationConfig config = objectWriter.getConfig();
            if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) &&
                    config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                objectWriter = objectWriter.with(this.ssePrettyPrinter);
            }
            objectWriter.writeValue(generator, value);

            writeSuffix(generator, object);
            generator.flush();
        }
        catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        }
        catch (JsonProcessingException ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
        }
    }

    protected JsonEncoding getJsonEncoding(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            Charset charset = contentType.getCharset();
            for (JsonEncoding encoding : JsonEncoding.values()) {
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }
        return JsonEncoding.UTF8;
    }

    protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        return typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
    }

    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
    }
}
