CREATE TABLE public.garage (
	id serial NOT null,
	"name" varchar NOT NULL,
	address text NOT NULL,
	creation_date date NOT NULL,
	max_car_capacity integer NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE public.car (
	licence_id varchar NOT NULL,
	id serial NOT NULL,
	brand varchar NOT NULL,
	model varchar NOT NULL,
	price real NOT NULL,
	garage_id int4 NULL,
	PRIMARY KEY (id),
	CONSTRAINT car_fk FOREIGN KEY (garage_id) REFERENCES public.garage(id) ON DELETE CASCADE
);
