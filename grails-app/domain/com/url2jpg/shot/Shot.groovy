package com.url2jpg.shot

import java.util.Date;

class Shot {
	String url
	String hash
	Date dateCreated

    static constraints = {
		url unique: true, blank: false, nullable: false
		hash unique: true, blank: false, nullable: false
    }
}
