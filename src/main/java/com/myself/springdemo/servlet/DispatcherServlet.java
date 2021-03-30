package com.myself.springdemo.servlet;

import com.myself.springdemo.annotation.AutoWired;
import com.myself.springdemo.annotation.Controller;
import com.myself.springdemo.annotation.Service;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet extends HttpServlet {

	private final Properties contextConfig = new Properties();

	private final Map<String, Object> beanMap = new ConcurrentHashMap<>();

	private final List<String> classNames = new ArrayList<>();

	private void doAutoWired() {
		if (beanMap.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
			Field[] fields = entry.getValue().getClass().getDeclaredFields();
			for (Field field : fields) {
				if (!field.isAnnotationPresent(AutoWired.class)) {
					continue;
				}

				AutoWired autoWired = field.getAnnotation(AutoWired.class);
				String beanName = autoWired.value().trim();
				if ("".equals(beanName)) {
					beanName = field.getType().getName();
				}
				field.setAccessible(true);
				try {
					field.set(entry.getValue(), beanMap.get(beanName));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void doRegistry() {

		if (classNames.isEmpty()) {
			return;
		}
		try {

			for (String className : classNames) {
				Class<?> aClass = Class.forName(className);

				// Spring中使用多个子方法来处理（策略模式 parseArray partseMap
				if (aClass.isAnnotationPresent(Controller.class)) {
					String beanName = lowerFirstCase(aClass.getSimpleName());
					// Srping中这个阶段是不是直接put instance的。这里put的是beanDefinition
					beanMap.put(beanName, aClass.newInstance());
				} else if (aClass.isAnnotationPresent(Service.class)) {
					Service service = aClass.getAnnotation(Service.class);

					// 默认用类名首字母注入
					// 如果定义了beanName，优先使用自己定义的beanName
					// 如果是一个接口，使用接口的类型注入

					// 在Spring中同样会分别调用不同的方法 autowiredByName autoWiredByType
					String beanName = service.value();
					if ("".equals(beanName.trim())) {
						beanName = lowerFirstCase(aClass.getSimpleName());
					}

					Object instance = aClass.newInstance();
					beanMap.put(beanName, instance);

					Class<?>[] interfaces = aClass.getInterfaces();
					for (Class<?> aninterface : interfaces) {
						beanMap.put(aninterface.getName(), aninterface);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doLoadDefinition(String packageName) {
		System.out.println(packageName);
		URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
		File files = new File(url.getFile());
		for (File file : files.listFiles()) {
			if (file.isDirectory()) {
				doLoadDefinition(packageName + "." + file.getName());
			} else {
				classNames.add(packageName + "." + file.getName().replace(".class", ""));
			}
		}
	}

	private void doLoadDefinitionConfig(String location) {

		// spring中通过Reader进行查找定位
		InputStream rs = getServletContext().getResourceAsStream(location);
		try {
			contextConfig.load(rs);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String lowerFirstCase(String str) {
		char[] chars = str.toCharArray();
		chars[0] += 32;
		return String.valueOf(chars);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
		System.out.println("=======开始=======");
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		// 初始化IOC容器

		//定位
		doLoadDefinitionConfig(config.getInitParameter("contextConfigLocation"));

		//加载
		doLoadDefinition(contextConfig.getProperty("mapperScan"));

		//注册
		doRegistry();

		//自动依赖注入
		//在Spring中调用getBean方法触发依赖注入
		doAutoWired();

		System.out.println();
		//SpringMVC增加HandlerMapping
	}


}
