package com.florianthom.kubernetesjoblauncher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KubernetesjoblauncherApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubernetesjoblauncherApplication.class, args);
	}

}
