create table messages (
	id bigint not null auto_increment,
	message_key varchar(80) not null,
	version integer not null,
	message_language varchar(8) default null,
	message varchar(4000) not null,
	is_useradded bit default 0,

	primary key (id),
	constraint uk_message_keylang unique (message_key, message_language)	
);

insert into messages (message_key, message, version) values
	("common.helloUser", "Hej {0}", 0),
	("emailHeader.imageUrl", "https://placehold.it/400x100", 0),
	("emailFooter.row1", "Gatan / Sverige", 0),
	("emailFooter.row2", "Från oss", 0),
	("forgottenPasswordEmail.title", "Har du glömt ditt lösenord?", 0),
	("forgottenPasswordEmail.subTitle", "Här får du en möjlighet att ange ett nytt", 0),
	("forgottenPasswordEmail.textRow1", "Du själv, eller någon annan, har fyllt i din e-postadress i applikationen Cordate för att få ange ett nytt lösenord. Det är därför som du får detta meddelande.", 0),
	("forgottenPasswordEmail.textRow2", "Klicka på knappen nedan för att få ange ett nytt lösenord. Knappen är endast giltig i 30 minuter. Om det inte är du som har begärt att ange ett nytt lösenord så kan du bara ignorera detta meddelade. Lösenordet kommer inte att ändras.", 0),
	("forgottenPasswordEmail.changePassword", "ANGE NYTT LÖSENORD", 0),
	("changedPasswordEmail.title", "Ditt lösenord har ändrats", 0),
	("changedPasswordEmail.subTitle", "Här får du en bekräftelse", 0),
	("changedPasswordEmail.textRow1", "Ditt lösenord i Cordate har ändrats. Om det är du själv som har gjort det så kan du ignorera detta meddelande. Annars bör du ta kontakt med administratören för Cordate.", 0);
