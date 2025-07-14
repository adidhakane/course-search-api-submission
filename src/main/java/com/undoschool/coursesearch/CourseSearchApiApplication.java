package com.undoschool.coursesearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
public class CourseSearchApiApplication {

	public static void main(String[] args) throws UnknownHostException {
		ConfigurableApplicationContext context = SpringApplication.run(CourseSearchApiApplication.class, args);

		Environment env = context.getEnvironment();
		String protocol = "http";
		String serverPort = env.getProperty("server.port", "8080");
		String contextPath = env.getProperty("server.servlet.context-path", "");
		String hostAddress = InetAddress.getLocalHost().getHostAddress();

		log.info("\n----------------------------------------------------------\n" +
						"🚀 Course Search API is running! Access URLs:\n" +
						"🏠 Local: \t\t{}://localhost:{}{}\n" +
						"🌐 External: \t{}://{}:{}{}\n" +
						"📊 Health Check: \t{}://localhost:{}{}/api/health\n" +
						"🔍 Search API: \t\t{}://localhost:{}{}/api/search\n" +
						"💡 Suggestions API: \t{}://localhost:{}{}/api/search/suggest\n" +
						"📚 Sample Query: \t{}://localhost:{}{}/api/search?q=math&size=3\n" +
						"----------------------------------------------------------",
				protocol, serverPort, contextPath,
				protocol, hostAddress, serverPort, contextPath,
				protocol, serverPort, contextPath,
				protocol, serverPort, contextPath,
				protocol, serverPort, contextPath,
				protocol, serverPort, contextPath);
	}
}
