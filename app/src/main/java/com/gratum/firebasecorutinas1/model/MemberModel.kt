package com.gratum.firebasecorutinas1.model

class MemberModel {

    companion object{
        const val CLOUD_FIRE_STORE_PATH = "MEMBER_TABLE"
        const val UUID_KEY= "UUID"
        const val ID_KEY = "ID"
        const val PASSWORD_KEY = "PASSWORD"
        const val EMAIL_KEY = "EMAIL"
        const val NICKNAME_KEY = "NICKNAME"
        const val PROFILE_IMAGE_URL_KEY = "PROFILE_IMAGE_URL"
        const val PROFILE_IMAGE_FILE_CLOUD_PATH_KEY = "PROFILE_IMAGE_FILE_CLOUD_PATH"
        const val MODIFY_DATE_KEY = "MODIFY_DATE"
        const val CREATE_DATE_KEY = "CREATE_DATE"
        const val CREATE_BY_KEY = "CREATE_BY"
        const val MODIFY_BY_KEY = "MODIFY_BY"
    }

    var uuId = ""
    var id = ""
    var password = "" //maybe you need encode this
    var email = ""
    var nickname = ""
    var profileImageUrl = ""
    var profileImageFileCloudPath = ""
    var createDate = ""
    var modifyDate = ""
    var createBy = ""
    var modifyBy = ""

    fun toDictionary(): MutableMap<String, Any>{

        val map = mutableMapOf<String,Any>()

        map[UUID_KEY] = uuId
        map[ID_KEY] = id
        map[PASSWORD_KEY] = password
        map[EMAIL_KEY] = email
        map[NICKNAME_KEY] = nickname
        map[PROFILE_IMAGE_URL_KEY] = profileImageUrl
        map[PROFILE_IMAGE_FILE_CLOUD_PATH_KEY] = profileImageFileCloudPath
        map[CREATE_DATE_KEY] = createDate
        map[MODIFY_DATE_KEY] = modifyDate
        map[CREATE_BY_KEY] = createBy
        map[MODIFY_BY_KEY] = modifyBy

        return map
    }

    fun parsing(map: MutableMap<String, Any>){
        uuId = map[UUID_KEY] as String
        id = map[ID_KEY] as String
        password = map[PASSWORD_KEY] as String
        email = map[EMAIL_KEY] as String
        profileImageUrl = map[PROFILE_IMAGE_URL_KEY] as String
        profileImageFileCloudPath = map[PROFILE_IMAGE_FILE_CLOUD_PATH_KEY] as String
        createDate = map[CREATE_DATE_KEY] as String
        modifyDate = map[MODIFY_DATE_KEY] as String
        createBy = map[CREATE_BY_KEY] as String
        modifyBy = map[MODIFY_BY_KEY] as String
    }
}