CREATE TABLE business_events (
  id VARCHAR(255) NOT NULL PRIMARY KEY,
  source VARCHAR(1024) NOT NULL,
  type VARCHAR(255) NOT NULL,
  spec_version VARCHAR(50) NOT NULL,
  time TIMESTAMP WITH TIME ZONE NOT NULL,
  data_schema VARCHAR(1024),
  data_content_type VARCHAR(255),
  data JSONB,
  subject VARCHAR(255)
);

CREATE UNIQUE INDEX ix_business_events_id
    ON business_events (id);

CREATE UNIQUE INDEX ix_business_events__source
    ON business_events (source);
