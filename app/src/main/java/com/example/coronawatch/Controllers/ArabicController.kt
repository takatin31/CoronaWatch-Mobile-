package com.example.coronawatch.Controllers

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

class ArabicController {

    companion object {
        fun encode_str(str: String) : String{
            try {
                return URLEncoder.encode(str, "utf-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return ""
        }

        fun decode_str(str: String) : String{
            try {
                return URLDecoder.decode(str, "utf-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            return ""
        }
    }


}