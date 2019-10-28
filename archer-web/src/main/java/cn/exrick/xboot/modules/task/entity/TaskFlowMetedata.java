package cn.exrick.xboot.modules.task.entity;

import com.google.api.client.util.Sets;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jdom.Element;

import java.util.Map;
import java.util.Set;

/**
 * Created by feng on 2019/10/28
 */
@Slf4j
@Data
public class TaskFlowMetedata {

	private Map<String, Element> vertexMap = Maps.newHashMap();

	private Set<Element> edgeSet = Sets.newHashSet();

	private TaskInstance instance;

}
