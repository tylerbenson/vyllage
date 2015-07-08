## GET /togglz-feature/{feature}/is-active
Returns whether a particular feature is currently active.
+ Parameters
	++ feature (string, `GOOGLE_ANALYTICS`) - The name of the feature.
	One of ( DUMMY_SUGGESTIONS | FACEBOOK_SDK | GOOGLE_ANALYTICS | NEW_RELIC | PRINTING | SHARE_RESUME | SUGGESTIONS | ZOPIM_MESSAGE_CLIENT )
+ Response 200.
	++ true / false.