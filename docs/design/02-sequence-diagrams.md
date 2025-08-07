# 🏷 브랜드 & 상품 (Brands / Products)

## 1. 브랜드 정보 조회

```mermaid
sequenceDiagram
	actor U as User
	participant BC as BrandController
	participant BS as BrandService
	participant BR as BrandRepository

	U ->> BC: 브랜드 정보 조회 요청 (brandId)
		activate U
		activate BC
			alt brandId가 없음
				BC -->> U: brandId가 없음
			end
			BC ->> BS:브랜드 정보 조회 (brandId)
			activate BS
				BS ->> BR: 브랜드 정보 조회 (brandId)
				activate BR
					BR -->> BS: 브랜드 정보 반환
				deactivate BR
				alt 브랜드 없음
					BS -->> BC: 404 Not Found
				else
					BS -->> BC: 브랜드 정보 반환
				end
			deactivate BS
			BC -->> U: 브랜드 정보 반환
		deactivate BC
	deactivate U
```

## 2. 상품 목록 조회

```mermaid
sequenceDiagram
	actor U as User
	participant PC as ProductController
	participant US as UserService
	participant BS as BrandService
	participant PS as ProductService
	participant LS as LikeService
	participant UR as UserRepository
	participant BR as BrandRepository
	participant PR as ProductRepository
	participant LR as LikeRepository

	U ->> PC: 상품 목록 조회 요청 (brandId, order, size, page)
	activate U
		activate PC
			alt X-HEADER-ID 가 null 이 아님
				PC ->> US: 사용자 정보 조회 (X-USER-ID)
				activate US
					US ->> UR: 사용자 정보 조회 (userId)
					activate UR
						UR -->> US: 사용자 정보 반환
					deactivate UR
					alt 사용자 정보 없음
						US -->> PC: 404 Not Found
					else
						US -->> PC: 사용자 정보 반환 (user)
					end
				deactivate US
			end
			alt brandId가 null이 아님
				PC ->> BS: 브랜드 정보 조회 (brandId)
				activate BS
					BS ->> BR: 브랜드 정보 조회 (brandId)
					activate BR
						BR -->> BS: 브랜드 정보 반환
					deactivate BR
					alt 브랜드 정보 없음
						BS -->> PC: 404 Not Found
					else
						BS -->> PC: 브랜드 정보 반환
					end
				deactivate BS
			end

			PC ->> PS: 상품 목록 조회 (user, brand, order, size, page)
			activate PS
				PS ->> PR: 조건에 맞는 상품 목록 조회 (user, brand, order, size, page)
				activate PR
					PR -->> PS: 조건에 맞는 상품 목록 반환
				deactivate PR
				alt 로그인
					loop productList
						PS ->> LS: 상품 좋아요여부 조회
						activate LS
						LS ->> LR: 상품 좋아요 여부 검색
							activate LR
								LR -->> LS: 상품 좋아요 여부 반환
							deactivate LR
						LS -->> PS: 상품 좋아요여부 반환
						deactivate LS
					end
				end
				PS -->> PC: 상품 목록 반환
			deactivate PS
		PC -->> U: 상품 목록 반환
		deactivate PC
	deactivate U

```

## 3. 상품 정보 조회

```mermaid
sequenceDiagram
	actor U as User
	participant PC as ProductController
	participant PS as ProductService
	participant US as UserService
	participant LS as LikeService
	participant UR as UserRepository
	participant PR as ProductRepository
	participant LR as LikeRepository

	U ->> PC: 상품 정보 조회 요청 (productId)
	activate U
		activate PC
			alt X-HEADER-ID 가 null 이 아님
				PC ->> US: 사용자 정보 조회 (X-USER-ID)
				activate US
					US ->> UR: 사용자 정보 조회 (userId)
					activate UR
						UR -->> US: 사용자 정보 반환
					deactivate UR
					alt 사용자 정보 없음
						US -->> PC: 404 Not Found
					else
						US -->> PC: 사용자 정보 반환 (user)
					end
				deactivate US
			end
			PC ->> PS: 상품 정보 조회 (productId)
			activate PS
				PS ->> PR: 상품 정보 조회 (productId)
				activate PR
					PR -->> PS: 상품 정보 반환
				deactivate PR
				alt 상품 없음
					PS -->> PC: 404 Not Found
				else
					PS-->> PC: 상품 정보 반환
				end
			deactivate PS
			alt 로그인
				PC ->> LS: 좋아요 여부 검색 (user, product)
				activate LS
					LS ->> LR: 좋아요 여부 검색 (user, product)
					activate LR
						LR -->> LS: 좋아요 여부 반환 (like)
					deactivate LR
					LS -->> PC: 상품 좋아요 여부 반환
				deactivate LS
			end

			PC -->> U: 상품 정보 반환
		deactivate PC
	deactivate U
```

# ❤️ 좋아요 (Likes)

## 4. 상품 좋아요 등록

```mermaid
sequenceDiagram
	actor U as User
	participant LC as LikeController
	participant US as UserService
	participant PS as ProductService
	participant LS as LikeService
	participant UR as UserRepository
	participant PR as ProductRepository
	participant LR as LikeRepository

	U ->> LC: 좋아요 등록 요청 (productId)
	activate U
	activate LC
		LC ->> US: 사용자 정보 조회 (X-USER-ID)
		activate US
			alt X-USER-ID 헤더 없음
				US -->> LC: 401 UnAuthorized
			end
			US ->> UR: 사용자 정보 조회 (userId)
			activate UR
				UR -->> US: 사용자 정보 반환
			deactivate UR
		alt 사용자 정보 없음
			US -->> LC: 401 UnAuthorized
		else
			US -->> LC: 사용자 정보 반환
		end
		deactivate US

		LC ->> PS: 상품 정보 조회 (productId)
		activate PS
			PS ->> PR: 상품 정보 조회 (productId)
			activate PR
				PR -->> PS: 상품 정보 반환
			deactivate PR
			alt 상품 미존재
				PS -->> LC: 404 NotFound
			else 상품 존재
				PS -->> LC: 상품 정보 반환
			end
		deactivate PS

		LC ->> LS: 좋아요 등록 처리 요청 (user, product)
		activate LS
			LS ->> LR: 좋아요 존재 확인
			activate LR
				LR -->> LS: 좋아요 존재 여부 반환
			deactivate LR
			alt 이미 등록된 좋아요
				LS -->> LC: 409 Conflict
			else 좋아요 등록 처리
				LS -->> LR: 좋아요 등록
				activate LR
					alt 저장 실패 (사유 불문)
						LR -->> LS: 500 Internal Server Error
					else 저장 성공
						LR -->> LS: 좋아요 등록 결과
					end
				deactivate LR
			end
			LS -->> LC: 좋아요 등록 결과 반환
			deactivate LS
		LC -->> U: 좋아요 등록 결과 반환
		deactivate LC
	deactivate U
```

## 5. 상품 좋아요 취소

```mermaid
sequenceDiagram
	actor U as User
	participant LC as LikeController
	participant US as UserService
	participant PS as ProductService
	participant LS as LikeService
	participant UR as UserRepository
	participant PR as ProductRepository
	participant LR as LikeRepository

	U ->> LC: 좋아요 취소 요청 (productId)
	activate U
	activate LC
		LC ->> US: 사용자 정보 조회 (X-USER-ID)
		activate US
			alt X-USER-ID 헤더 없음
				US -->> LC: 401 UnAuthorized
			end
			US ->> UR: 사용자 정보 조회 (userId)
			activate UR
				UR -->> US: 사용자 정보 반환
			deactivate UR
		alt 사용자 정보 없음
			US -->> LC: 401 UnAuthorized
		else
			US -->> LC: 사용자 정보 반환
		end
		deactivate US

		LC ->> PS: 상품 정보 조회 (productId)
		activate PS
			PS ->> PR: 상품 정보 조회 (productId)
			activate PR
				PR -->> PS: 상품 정보 반환
			deactivate PR
			alt 상품 미존재
				PS -->> LC: 404 NotFound
			else 상품 존재
				PS -->> LC: 상품 정보 반환
			end
		deactivate PS

		LC ->> LS: 좋아요 취소 처리 요청 (user, product)
		activate LS
			LS ->> LR: 좋아요 존재 확인
			activate LR
				LR -->> LS: 좋아요 존재 여부 반환
			deactivate LR
			alt 좋아요하지 않은 상품
				LS -->> LC: 409 Conflict
			else 좋아요 취소 처리
				LS -->> LR: 좋아요 취소
				activate LR
					alt 저장 실패 (사유 불문)
						LR -->> LS: 500 Internal Server Error
					else 저장 성공
						LR -->> LS: 좋아요 취소 결과
					end
				deactivate LR
			end
			LS -->> LC: 좋아요 취소 결과 반환
			deactivate LS
		LC -->> U: 좋아요 취소 결과 반환
		deactivate LC
	deactivate U
```

## 6. 내가 좋아요 한 상품 목록 조회

```mermaid
sequenceDiagram
	actor U as User
	participant LC as LikeController
	participant US as UserService
	participant LS as LikeService
	participant UR as UserRepository
	participant LR as LikeRepository

	U ->> LC: 좋아요한 상품 목록 조회 요청
	activate U
		activate LC
			LC ->> US: 사용자 정보 조회 (X-USER-ID)
			activate US
				alt X-USER-ID 헤더 없음
					US -->> LC: 401 UnAuthorized
				end
				US ->> UR: 사용자 정보 조회 (userId)
				activate UR
					UR -->> US: 사용자 정보 반환
				deactivate UR
			alt 사용자 정보 없음
				US -->> LC: 401 UnAuthorized
			else
				US -->> LC: 사용자 정보 반환
			end
			deactivate US

			LC ->> LS: 좋아요한 상품 조회 (user)
			activate LS
				LS ->> LR: 좋아요한 상품 조회 (user)
				activate LR
					LR -->> LS: 좋아요한 상품 목록 반환
				deactivate LR
				LS -->> LC: 좋아요한 상품 목록 반환
			deactivate LS
			LC -->> U: 좋아요한 상품 목록 반환
		deactivate LC
	deactivate U


```

# 🛒 장바구니 (Carts)

## 7. 장바구니에 담기

```mermaid
sequenceDiagram
	actor U as User
	participant CC as CartController
	participant US as UserService
	participant PS as ProductService
	participant CS as CartService
	participant UR as UserRepository
	participant PR as ProductRepository
	participant CR as CartRepository

	U ->> CC: 장바구니에 상품 담기 요청 (productId, quantity)
	activate U
		activate CC
			CC ->> US: 사용자 정보 조회 (X-USER-ID)
			activate US
				alt X-USER-ID 헤더 없음
					US -->> CC: 401 UnAuthorized
				end
				US ->> UR: 사용자 정보 조회 (userId)
				activate UR
					UR -->> US: 사용자 정보 반환
				deactivate UR
			alt 사용자 정보 없음
				US -->> CC: 401 UnAuthorized
			else
				US -->> CC: 사용자 정보 반환
			end
			deactivate US

			CC ->> PS: 상품 정보 조회(productId)
			activate PS
				PS -->> PR: 상품 정보 조회 (productId)
				activate PR
					PR -->> PS: 상품 정보 반환
				deactivate PR
				alt 상품 조회 실패
					PS -->> CC: 404 Not Found
				else 품절인 상품
					PS -->> CC: 409 Conflict
				else 재고 < 수량:
					PS -->> CC: 409 Conflict
				else
					PS -->> CC: 상품 정보 반환
				end
			deactivate PS

			CC ->> CS: 장바구니에 상품 추가 (user, product, quantity)
			activate CS
				CS ->> CR: 장바구니에 이미 상품이 있는지 확인
				activate CR
					CR -->> CS: 상품 반환
					alt 상품이 이미 장바구니에 존재
						CS ->> CR: 장바구니에 상품 수량을 quantity로 업데이트
						CR -->> CS: 업데이트 현황 반환
					else 상품이 장바구니에 없으면
						CS ->> CR: 장바구니에 해당 상품 등록 (user, product, quantity)
						CR -->> CS: 장바구니에 넣기 현황 반환
					end
				deactivate CR
			CS -->> CC: 장바구니에 상품 넣기 결과 반환
			deactivate CS
		CC -->> U: 장바구니에 상품 넣기 결과 반환
		deactivate CC
	deactivate U


```

## 8. 장바구니에서 삭제

```mermaid
sequenceDiagram
	actor U as User
	participant CC as CartController
	participant US as UserService
	participant PS as ProductService
	participant CS as CartService
	participant UR as UserRepository
	participant PR as ProductRepository
	participant CR as CartRepository

	U ->> CC: 장바구니에 상품 삭제 요청 (productId, quantity)
	activate U
		activate CC
			CC ->> US: 사용자 정보 조회 (X-USER-ID)
			activate US
				alt X-USER-ID 헤더 없음
					US -->> CC: 401 UnAuthorized
				end
				US ->> UR: 사용자 정보 조회 (userId)
				activate UR
					UR -->> US: 사용자 정보 반환
				deactivate UR
			alt 사용자 정보 없음
				US -->> CC: 401 UnAuthorized
			else
				US -->> CC: 사용자 정보 반환
			end
			deactivate US

			CC ->> PS: 상품 정보 조회(productId)
			activate PS
				PS -->> PR: 상품 정보 조회 (productId)
				activate PR
					PR -->> PS: 상품 정보 반환
				deactivate PR
				alt 상품 조회 실패
					PS -->> CC: 404 Not Found
				else
					PS -->> CC: 상품 정보 반환
				end
			deactivate PS

			CC ->> CS: 장바구니에 상품 삭제 (user, product, quantity)
			activate CS
				CS ->> CR: 장바구니에 상품이 있는지 확인
				activate CR
					CR -->> CS: 상품 반환
					alt 상품이 장바구니에 없음
						CS -->> CC: 400 BadRequest
					else
					CS ->> CR: 장바구니에 해당 상품 삭제 (user, product)
					CR -->> CS: 장바구니 삭제 결과 반환
					end
				deactivate CR
			CS -->> CC: 장바구니 상품 삭제 결과 반환
			deactivate CS
		CC -->> U: 장바구니 상품 삭제 결과 반환
		deactivate CC
	deactivate U

```

## 9. 장바구니 리스트 확인

```mermaid
sequenceDiagram
	actor U as User
	participant CC as CartController
	participant US as UserService
	participant CS as CartService
	participant UR as UserRepository
	participant CR as CartRepository



	U ->> CC: 장바구니 상품 리스트 조회 요청 (productId, quantity)
	activate U
		activate CC
			CC ->> US: 사용자 정보 조회 (X-USER-ID)
			activate US
				alt X-USER-ID 헤더 없음
					US -->> CC: 401 UnAuthorized
				end
				US ->> UR: 사용자 정보 조회 (userId)
				activate UR
					UR -->> US: 사용자 정보 반환
				deactivate UR
			alt 사용자 정보 없음
				US -->> CC: 401 UnAuthorized
			else
				US -->> CC: 사용자 정보 반환
			end
			deactivate US

			CC ->> CS: 장바구니 리스트 조회 요청 (user)
			activate CS
				CS ->> CR: 장바구니 리스트 조회 (user)
				activate CR
					CR -->> CS: 장바구니 리스트 반환
				deactivate CR
				CS -->> CC : 장바구니 상품 리스트 조회 결과 반환
			deactivate CS
		CC -->> U: 장바구니 상품 리스트 조회 결과 반환
		deactivate CC
	deactivate U


```

# 🧾 주문 / 결제 (Orders)

## 10. 주문 요청

```mermaid
sequenceDiagram
	actor U as User
	participant OC as OrderController
	participant US as UserService
	participant PS as ProductService
	participant PR as ProductRepository
	participant OS as OrderService
	participant CS as CouponService
	participant CDS as CouponDomainService
	participant TP as ThirdParty
	participant UR as UserRepository
	participant OR as OrderRepository
	participant CR as CouponRepository

	U ->> OC: 주문 요청 (productList(productId, quantity), totalPrice)
	activate U
		activate OC
			OC ->> US: 사용자 정보 조회 (X-USER-ID)
			activate US
				alt X-USER-ID 헤더 없음
					US -->> OC: 401 UnAuthorized
				end
				US ->> UR: 사용자 정보 조회 (userId)
				activate UR
					UR -->> US: 사용자 정보 반환
				deactivate UR
				alt 사용자 정보 없음
					US -->> OC: 401 UnAuthorized
				else
					US -->> OC: 사용자 정보 반환
				end
			deactivate US

			OC ->> PS: 상품 정보 조회 (productList(productId, quantity))
			activate PS
				loop 각 상품마다
					PS ->> PR: 상품 정보 조회 (productId)
					activate PR
						PR -->> PS: 상품 정보 반환 (product)
					deactivate PR
					alt 상품 미존재
						PS -->> OC: 404 Not Found
					end
				end
				PS -->> OC: 상품 리스트 반환
			deactivate PS

			OC ->> CS: 쿠폰 정보 조회 (couponId, user)
			activate CS
				CS ->> CR: 쿠폰 정보 조회
				activate CR
					CR -->> CS: 쿠폰 정보 반환
				deactivate CR
				CS ->> CDS: 사용 가능한 쿠폰인지 확인
				activate CDS
					alt 쿠폰이 존재하지 않는다면
						CDS -->> CS : 400 Not Found
					else 사용자의 쿠폰이 아니라면
						CDS -->> CS : 403 Forbidden
					else 이미 사용한 쿠폰이라면
						CDS -->> CS: 409 Conflict
					else
						CDS -->> CS: 쿠폰 반환
					end
				CDS -->> CS: 쿠폰 정보 반환
			deactivate CDS
			CS -->> OC: 쿠폰 정보 반환
			deactivate CS

			OC ->> OS: 상품 주문 요청 (user, productList(product), totalPrice, coupon)
			activate OS
				activate OS
					OS -->> OS: 전체 가격, 사용자 포인트 확인
					OS -->> OS: 가격 유효성 확인
				deactivate OS
				alt 사용자 포인트 < totalPrice
					OS -->> OC: 400 BadRequest
				else
					activate OS
						OS -->> OS: 사용자 포인트 -= totalPrice (도메인에서 진행)
					deactivate OS
				end
				loop 각 상품마다
					alt 삼품재고 < 수량
						OS -->> OC: 409 Conflict
					else
						activate OS
							OS -->> OS: 상품 재고 -= 주문 수량 (도메인에서 진행)
						deactivate OS
					end
				end
				OS ->> OR: 주문 정보 저장
				activate OR
					OR -->> OS: 주문 정보 저장 결과 반환
				deactivate OR
			OS -->> OC: 주문 요청 결과 반환
			deactivate OS
		activate TP
			OC -->> TP: 주문 정보 전송 (비동기)
		deactivate TP
		OC -->> U: 주문 요청 결과 반환
		deactivate OC
	deactivate U

```

## 11. 유저의 주문 목록 조회

```mermaid
sequenceDiagram
	actor U as User
	participant OC as OrderController
	participant OS as OrderService
	participant US as UserService
	participant UR as UserRepository
	participant OR as OrderRepository

	U ->> OC: 주문 목록 조회 요청 (startDate, endDate)
	activate U
		activate OC
			OC ->> US: 사용자 정보 조회 (X-USER-ID)
			activate US
				alt X-USER-ID 헤더 없음
					US -->> OC: 401 UnAuthorized
				end
				US ->> UR: 사용자 정보 조회 (userId)
				activate UR
					UR -->> US: 사용자 정보 반환
				deactivate UR
				alt 사용자 정보 없음
					US -->> OC: 401 UnAuthorized
				else
					US -->> OC: 사용자 정보 반환
				end
			deactivate US
			OC ->> OS: 주문 목록 조회 요청 (user, startDate, endDate)
			activate OS
				OS ->> OR: 주문 목록 조회 (startDate, endDate)
				activate OR
					OR -->> OS: 주문 목록 반환
				deactivate OR
				OS -->> OC: 주문 목록 반환
			deactivate OS
			OC -->> U: 주문 목록 반환
		deactivate OC
	deactivate U
```

## 12. 단일 주문 상세 조회

```mermaid
sequenceDiagram
	actor U as User
	participant OC as OrderController
	participant OS as OrderService
	participant US as UserService
	participant UR as UserRepository
	participant OR as OrderRepository


	U ->> OC: 주문 상세 조회 요청 (orderId)
	activate U
		activate OC
			OC ->> US: 사용자 정보 조회 (X-USER-ID)
			activate US
				alt X-USER-ID 헤더 없음
					US -->> OC: 401 UnAuthorized
				end
				US ->> UR: 사용자 정보 조회 (userId)
				activate UR
					UR -->> US: 사용자 정보 반환
				deactivate UR
				alt 사용자 정보 없음
					US -->> OC: 401 UnAuthorized
				else
					US -->> OC: 사용자 정보 반환
				end
			deactivate US
			OC ->> OS: 주문 상세 조회 요청 (user, orderId)
			activate OS
				OS ->> OR: 주문 상세 조회 (orderId)
				activate OR
					OR -->> OS: 주문 정보 반환
				deactivate OR
				alt 주문이 존재하지 않는 경우
					OS -->> OC: 404 Not Found
				end
				alt 주문자와 로그인 사용자가 다른 경우
					OS -->> OC: 403 Forbidden
				else
					OS -->> OC: 주문 상세 반환
				end
			deactivate OS

			alt 쿠폰 사용 시
				OC ->> CS: 쿠폰 조회
				activate CS
					CS ->> CR: 쿠폰 정보 조회
					activate CR
						CR -->> CS: 쿠폰 정보 반환
					deactivate CR
					CS -->> OC: 쿠폰 정보 반환
				deactivate CS
			end
			OC -->> U: 주문 상세 반환
		deactivate OC
	deactivate U

```

# 🏷️ 쿠폰 (Coupon)

## 13. 쿠폰 리스트 조회

```mermaid
sequenceDiagram
	actor U as User
	participant CC as CouponController
	participant US as UserService
	participant CS as CouponService
	participant UR as UserRepository
	participant CR as CouponRepository

	U ->> CC: 쿠폰 리스트 조회 요청
	activate U
		activate CC
			CC ->> US: 사용자 정보 조회 (X-USER-ID)
			activate US
				alt X-USER-ID 헤더 없음
					US -->> CC: 401 UnAuthorized
				end
				US ->> UR: 사용자 정보 조회 (userId)
				activate UR
					UR -->> US: 사용자 정보 반환
				deactivate UR
				alt 사용자 정보 없음
					US -->> CC: 401 UnAuthorized
				else
					US -->> CC: 사용자 정보 반환
				end
			deactivate US
			CC ->> CS: 쿠폰 리스트 조회
			activate CS
				CS ->> CR: 쿠폰 리스트 조회
				activate CR
					CR -->> CS: 쿠폰 리스트 반환
				deactivate CR
			CS -->> CC: 쿠폰 리스트 반환
			deactivate CS
		CC -->> U: 사용자 쿠폰 리스트 반환
		deactivate CC
	deactivate U

```
