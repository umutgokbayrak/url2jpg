package com.url2jpg.hash

import org.apache.commons.lang.StringUtils

import com.url2jpg.shot.Shot

class HashController {

    def index = {
		if (params.url) {
			if (params.k == 'Hebelek') {
				String url = URLDecoder.decode(params.url, 'utf-8')
				url = StringUtils.replace(url, ' ', '%20')
				if (url.indexOf('http') != 0) url = "http://${url}"
				
				def results = Shot.createCriteria().list() {
					eq('url', url)
					gt('dateCreated', new Date() - 30)
				}

				String output = '-1'
				if (results.size() > 0) {
					output = results[0].hash
				}
				render text: output
			} else {
				render text: 'auth'
			}
		} else {
			render text: 'fail'
		}
	}
}
