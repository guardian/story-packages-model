namespace scala storypackage.thrift

/* The event type describe the resource state ii*/
enum EventType {
    Update = 1,
    Delete = 2
}

/* Describes the types of articles that we can have */

enum ArticleType {
    Article = 1,
    Snap = 2
}

/**
* this stucture represents articles and its overrides
**/
struct Article {

    1: required string id;

    2: required ArticleType articleType;

    3: optional string headline;

    4: optional string href;

    5: optional string trailText;

    6: optional string imageSrc;

    7: optional bool isBoosted;

    8: optional bool imageHide;

    9: optional bool showMainVideo;

    10: optional bool showKickerTag;

    11: optional bool showKickerSection;

    12: optional string byline;

    13: optional string customKicker;

    14: optional bool showBoostedHeadline;

    15: optional bool showQuotedHeadline;
}

struct Event {

    1: required EventType eventType;

    2: required string packageId;

    3: required list<Article> articles;

}

