package com.mimacom.demo.kafka.reactive.basic;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.reactive.FluxSender;
import org.springframework.cloud.stream.reactive.StreamEmitter;

@SpringBootApplication
@EnableBinding({ Source.class, Sink.class })
public class BasicApplication {

	private final static Logger LOG = LoggerFactory.getLogger(BasicApplication.class);

	private static final Random RANDOM = new Random();

	private static final String[] NAMES = new String[] {
			"Aaron", "Barbara", "Charlie", "Dennis", "Emily", "Freddie",
			"Grace", "Harry", "Irene", "Jack", "Kevin", "Lily",
			"Monica", "Noah", "Oscar", "Paul", "Ralph", "Sophia",
			"Thomas", "Ursula", "Veronica", "Walter", "Xavier", "Yvonne", "Zoe"
	};

	public static void main(String[] args) {
//		System.setProperty("spring.main.lazy-initialization", "true");
		SpringApplication.run(BasicApplication.class, args);
	}

	@StreamEmitter
	@Output(Source.OUTPUT)
	public void emit(FluxSender fluxSender) {
		fluxSender.send(Flux.interval(Duration.ofSeconds(1L))
				.map(__ -> new MyBean(getRandomUuid(), getRandomName())));
	}

	@StreamListener(Sink.INPUT)
	public void receive(Flux<MyBean> input) {
		input
				.map(order -> order.toString())
				.subscribe(LOG::info);
	}

	private String getRandomUuid() {
		return UUID.randomUUID().toString();
	}

	private String getRandomName() {
		return NAMES[RANDOM.nextInt(NAMES.length)];
	}
}

class MyBean {
	private String id;

	private String name;

	public MyBean() {
	}

	public MyBean(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "MyBean{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
