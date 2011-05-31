package com.url2jpg.grab

import grails.converters.JSON

import javax.annotation.PostConstruct

import org.apache.commons.lang.StringUtils

import com.url2jpg.shot.Shot

class GrabController {

	def grailsApplication
	def script
	
	@PostConstruct
	def init() {
		script = grailsApplication.config.url2jpg.path
	}
	
    def index = {
		if (params.url) {
			if (params.k == 'Hebelek') {
				String url = URLDecoder.decode(params.url, 'utf-8')
				url = StringUtils.replace(url, ' ', '%20')
				if (url.indexOf('http') != 0) url = "http://${url}"
				
				boolean validUrl = true
				try {
					URL parsedUrl = new URL(url)
					String host = parsedUrl.getHost()
					InetAddress inetAddress = InetAddress.getByName(host)
					String ip = inetAddress.getHostAddress()
					if (ip.startsWith('127.') || ip.startsWith('192.') || ip.startsWith('10.') || ip.indexOf(':') > 0) {
						validUrl = false
					}
				} catch (e) { 
					validUrl = false
					render text: 'invalid_url'
				}
				
				if (validUrl) {
					def results = Shot.createCriteria().list() {
						eq('url', url)
						gt('dateCreated', new Date() - 30)
					}
					
					String output
					if (results.size() > 0) {
						output = results[0].hash
					} else {
						def url2jpg = "${script} ${url} 10000".execute()
						String scriptOut = url2jpg.inputStream.text
						
						def json = JSON.parse("{ ${StringUtils.substringBetween(scriptOut, '{', '}')} }")
				
						switch (json.result) {
							case '0':
								String hash = json.hash
								output = hash
								new Shot(url: url, hash: hash).save()
								break
							case '5': output = 'retry'; break
							case '1': output = 'system'; break
							default: output = 'unknown_err'; break
						}
					}
					render text: output
				} else {
					render text: 'noop'
				}
			} else {
				render text: 'auth'
			}
		} else {
			render text: 'fail'
		}
	}
}
