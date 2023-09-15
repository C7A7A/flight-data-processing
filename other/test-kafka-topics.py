from time import sleep
from json import dumps
from kafka import KafkaProducer

if __name__ == "__main__":
	kafka_topic = "flights-test"

	producer = KafkaProducer(
		bootstrap_servers=['[::1]:9092'],
		value_serializer=lambda x:
		dumps(x).encode('utf-8')
	)

	if producer.bootstrap_connected():
		print("CONNECTED TO KAFKA!")
		for e in range(1000):
			data = {'number': e}
			producer.send(kafka_topic, value=data)
			print(f"sending data: {data}")
			sleep(5)
	else:
		print("NOT CONNECTED!")
