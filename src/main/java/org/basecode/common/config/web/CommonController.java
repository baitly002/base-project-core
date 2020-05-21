package org.basecode.common.config.web;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Appender;
import com.dashu.lazyapidoc.annotation.Doc;
import lombok.extern.slf4j.Slf4j;
import org.basecode.common.dict.DictGroupInfo;
import org.basecode.common.dict.LocalCache;
import org.basecode.common.criterion.exception.BusinessException;
import org.basecode.common.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@Doc("数据字典")
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {


    @Doc("获取最新版本数据字典")
    @Doc(name = "version", value = "现版本号", remark = "")
    @GetMapping("dict/{version}")
    public DictGroupInfo getLatestDict(@PathVariable String version){
        if("0".equals(version)){
            //返回最新的
            return loadLatestDict();
        }else{
            Long latestVersion = 0l;
            if(StringUtils.isNotBlank(LocalCache.versionId)){
                latestVersion = Long.parseLong(LocalCache.versionId);
            }
            Long versionRequest = Long.parseLong(version);
            if(versionRequest<latestVersion){
                //数据字典是旧的 返回新字典数据

                return loadLatestDict();
            }else{
                //数据字典没有变动 返回null
                return null;
            }
        }
    }

    public DictGroupInfo loadLatestDict(){
        DictGroupInfo dictGroupInfo = new DictGroupInfo();
        DictGroupInfo dictGroupInfoCache = LocalCache.dictGroups;
        if(dictGroupInfoCache != null) {
            dictGroupInfo.setDict(dictGroupInfoCache.getDict());
            dictGroupInfo.setVersionId(LocalCache.versionId);
            return dictGroupInfo;
        }else{
            throw new BusinessException("20000010", "本地找不到数据字典信息！");
        }
    }

    @GetMapping("update/log/level")
    public Boolean updateLogLevel(String name, String level) {
//        LogbackLoggingSystem
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        // 将项目日志打印级别设置成debug级别，还可以根据包路径进行日志级别设置
        loggerContext.getLogger(name).setLevel(Level.toLevel(level));
        return true;
    }
    @GetMapping("update/log/appender")
    public Boolean updateLogAppender(String appender, @RequestParam(defaultValue = "true") Boolean enable) {
//        LogbackLoggingSystem
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Appender ad = loggerContext.getLogger("root").getAppender(appender);
        if(ad != null){
            boolean started = ad.isStarted();
            if(started && !enable){
                ad.stop();
            }
            if(!started && enable){
                ad.start();
            }
        }
        return true;
    }
}
