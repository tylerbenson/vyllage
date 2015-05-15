## GET /togglz-feature/{feature}/is-active
Returns whether a particular feature is currently active.
+ Parameters
	++ feature (string, `GOOGLE_ANALYTICS`) - The name of the feature. One of ( GOOGLE_ANALYTICS | NEW_RELIC | ZOPIM_MESSAGE_CLIENT | SHARE_RESUME )
+ Response 200.
	++ true / false.