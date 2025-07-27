```mermaid
erDiagram
	USER {
		bigint id PK
		string login_id
		string gender
		date birth_date
		string email
		timestamp created_at
		timestamp updated_at
		timestamp deleted_at
	}

	BRAND {
		bigint id PK
		string name
		timestamp created_at
		timestamp updated_at
		timestamp deleted_at
	}

	PRODUCT {
		bigint id PK
		bigint brand_id FK
		string name
		long price
		long quantity
		string status
		timestamp created_at
		timestamp updated_at
		timestamp deleted_at
	}

	ORDERS {
		bigint id PK
		bigint user_id FK
		timestamp ordered_at
		long total_price
		timestamp created_at
		timestamp updated_at
		timestamp deleted_at
	}

	ORDERS_ITEM {
		bigint id PK
		bigint order_id FK
		bigint product_id FK
		long price
		long quantity
		timestamp created_at
		timestamp updated_at
		timestamp deleted_at
	}

	CART_ITEM {
		bigint id PK
		bigint user_id FK
		bigint product_id FK
		long quantity
		timestamp added_at
		timestamp created_at
		timestamp updated_at
		timestamp deleted_at
	}

	LIKE {
		bigint id PK
		bigint user_id FK
		bigint product_id FK
		timestamp liked_at
		timestamp created_at
		timestamp updated_at
		timestamp deleted_at
	}

	BRAND ||--o{ PRODUCT : contains
	ORDERS ||--|{ ORDERS_ITEM : contains
	PRODUCT ||--o{ ORDERS_ITEM : referenced
	USER ||--o{ CART_ITEM : referenced
	USER ||--o{ ORDERS : contains
	USER ||--o{ LIKE : referenced
	PRODUCT ||--o{ LIKE : referenced
```
