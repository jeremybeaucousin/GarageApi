CREATE TABLE public.garage (
	"name" varchar NOT NULL,
	address text NOT NULL,
	creation_date date NOT NULL,
	max_car_capacity integer NOT NULL,
	id serial NOT NULL
	PRIMARY KEY (id)
);

CREATE TABLE public.car (
	licence_id varchar NOT NULL,
	id serial NOT NULL,
	brand varchar NOT NULL,
	model varchar NOT NULL,
	price varchar NOT NULL,
	garage_id int4 NULL,
	PRIMARY KEY (id),
	CONSTRAINT car_fk FOREIGN KEY (garage_id) REFERENCES public.garage(id);
);