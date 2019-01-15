package com.phpusr.wildrace.dto.vk

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

class WallGet {
    lateinit var response: WallGetResponse
}

class WallGetResponse {
    var count = -1L
    lateinit var items: List<VKPost>
    lateinit var profiles: List<VKProfile>
    lateinit var groups: List<VKGroup>
}

@JsonIgnoreProperties("owner_id", "marked_as_ads", "post_type", "can_edit", "created_by", "can_delete",
        "post_source", "comments", "likes", "reposts")
class VKPost {
    var id = -1L
    var from_id = -1L
    var date = -1L
    lateinit var text: String
}

@JsonIgnoreProperties("screen_name", "online")
class VKProfile {
    var id = -1L
    lateinit var first_name: String
    lateinit var last_name: String
    var sex: Byte? = null
    var photo_50: String? = null
    var photo_100: String? = null
}

@JsonIgnoreProperties("screen_name", "is_closed", "type")
class VKGroup {
    var id = -1L
    lateinit var name: String
    var photo_50: String? = null
    var photo_100: String? = null
    var photo_200: String? = null
}