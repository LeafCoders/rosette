insert into messages (message_key, message, version) values
	("welcomeEmail.title", "Välkommen till Cordate!", 0),
	("welcomeEmail.subTitle", "Snart kan du logga in", 0),
	("welcomeEmail.textRow1", "Ett konto på Cordate har skapats för dig. En administratör på Cordate måste först aktivera ditt konto innan du kan logga in. Du får ett nytt meddelande när det är klart.", 0),
	("activatedUserEmail.title", "Ditt konto har aktiverats", 0),
	("activatedUserEmail.subTitle", "Nu kan du logga in", 0),
	("activatedUserEmail.textRow1", "Ditt konto har fått de rättigheter som gäller för de grupper som du angav när du skapade ditt konto. Om du saknar några rättigheter så kan du använda \"Hjälp mig\"-menyn för att be om fler rättigheter.", 0),
	("activatedUserEmail.login", "Logga in", 0);
	
create table consents (
	id bigint not null auto_increment,
	version integer not null,
	type varchar(20) not null,
	source varchar(20) not null,
	time datetime not null,
	user_id bigint not null,
	consent_text varchar(4000) not null,

	primary key (id)	
);
