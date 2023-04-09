// =============
// 시작 - 지도 초기 로딩
const container = document.getElementById("map"); //지도를 담을 영역의 DOM 레퍼런스

const options = {
	//지도를 생성할 때 필요한 기본 옵션
	// center: new kakao.maps.LatLng(crd.latitude, crd.longitude), //지도의 중심좌표.
	center: new kakao.maps.LatLng(37.498004414546934, 127.02770621963765), //지도의 중심좌표.
	level: 4, //지도의 레벨(확대, 축소 정도)
};

const map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴
// 끝 - 지도 초기 로딩
// =============

// =============
// 시작 - 초기 로딩시 내 위치로 이동
// moveToCurrent();
// 끝 - 초기 로딩시 내 위치로 이동
// =============

// =============
// 시작 - 지도 컨트롤러 추가
// 일반 지도와 스카이뷰로 지도 타입을 전환할 수 있는 지도타입 컨트롤을 생성합니다
const mapTypeControl = new kakao.maps.MapTypeControl();

// 지도에 컨트롤을 추가해야 지도위에 표시됩니다
// kakao.maps.ControlPosition은 컨트롤이 표시될 위치를 정의하는데 TOPRIGHT는 오른쪽 위를 의미합니다
map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

// 지도 확대 축소를 제어할 수 있는  줌 컨트롤을 생성합니다
const zoomControl = new kakao.maps.ZoomControl();
map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
// 끝 - 지도 컨트롤러 추가
// =============

// =============
// 시작 - 공통 함수 영역
// 1. 해당 위치로 맵 이동하기
function movePosition(lat, lon) {
	const moveLatLon = new kakao.maps.LatLng(lat, lon);
	map.panTo(moveLatLon);
}

// 2. 내 현재 위치로 맵 이동하기
// geolocation 객체에서 내 현재 위치 불러오기
function moveToCurrent() {
	navigator.geolocation.getCurrentPosition(mapInitSuccess, mapInitError);
}
// 위치 얻기 성공시
function mapInitSuccess(pos) {
	const crd = pos.coords;
	// console.log(crd);
	movePosition(crd.latitude, crd.longitude);
}
// 에러 발생시
function mapInitError(err) {
	console.log(err);
}

// 3. 위치 배열 및 마커 배열 초기화
function positionInit() {
	for (let x = 0; x < markPositions.length; x++) {
		markPositions[x].length = 0;
	}
	for (let x = 0; x < markMarkers.length; x++) {
		for (let y = 0; y < markMarkers[x].length; y++) {
			markMarkers[x][y].setMap(null);
		}
		markMarkers[x].length = 0;
	}
}
// 끝
// =============

// =============
// 시작 - 마커 표시 메뉴 및 마커 표시
/** 마커 생성 서순
 * 1. 위도, 경도를 fetch로 받아와서 locationArray 를 생성한다.
 * 		[데이터 타입: kakao.maps.LatLng(위도 latitude, 경도 longitude)]
 * 2. locationArray의 요소들로 createMarker(position, image)를 실행해 마커를 생성하고 마커 객체 배열을 생성한다.
 * 		[데이터 타입: kakao.maps.Marker({position: kakao.maps.LatLng(), image: kakao.maps.MarkerImage(src, size, options) })]
 * 3. MarkerImage는 createMarkerImage(이미지 url, new kakao.maps.Size(width, height), 옵션) 로 생성한다.
 * 		=> 내부에 kakao.maps.MarkerImage(src, size, options)를 실행함.
 * 4. create범주Markers()를 선언한다.
 * 		1) locationArray의 요소만큼 반복문 실행
 * 		2) imageSize: kakao.maps.Size(width, height)로 마커 이미지 사이즈 설정
 * 		3) imageOptions: {
 * 						spriteOrigin: 이미지 시작점. kakao.maps.Point(x좌표(세로), y좌표(가로)),
 * 						spriteSize : 이미지 크기. kakao.maps.Size(width, height)
 * 				}  를 생성
 * 		4) createMarkerImage(이미지 URL, imageSize, imageOptions) 로 markerImage 객체 생성
 * 		5) createMarker(position 객체, image 객체)로 마커를 생성
 * 		6) 생성된 마커를 마커 객체 배열에 추가 (push)
 * 5. set범주Markers(map) 을 선언한다.
 * 		1) 마커 객체 배열을 돌면서 Marker.setMap(map) 실행
 */
/** 범주 바꾸기 서순
 * 1. 각 범주 버튼 엘리먼트를 가져온다.
 * 2. 클릭되면 해당 엘리먼트의 클래스에 menu_selected를 추가해 스타일을 입힌다.
 * 3. set범주Marker(map)을 실행한다.
 * 4. 만약 마커를 없애고 싶다면 null을 넣어준다.
 */

// 마커이미지의 주소와, 크기, 옵션으로 마커 이미지를 생성하여 리턴하는 함수입니다
function createMarkerImage(src, size, options) {
	const markerImage = new kakao.maps.MarkerImage(src, size, options);
	return markerImage;
}

// 좌표와 마커이미지를 받아 마커를 생성하여 리턴하는 함수입니다
function createMarker(position, image) {
	const marker = new kakao.maps.Marker({
		position: position,
		image: image,
	});

	return marker;
}

const markPositions = [[], [], [], [], [], [], []];
const markMarkers = [[], [], [], [], [], [], []];

// markPositions 채우기

// 1. 백엔드로부터 현재 화면 영역 내부의 정보들을 가져온다.
async function getAreaInfo() {
	// 현재 보이는 영역의 중심좌표와 확대 정도를 fetch하여 해당 지역의 정보를 받아온다.
	// level당 단위길이 의미: 20m 30m 50m 100m 250m 500m 1km 2km 4km 8km 16km 32km 64km 128km
	const center = map.getCenter(); // 중심 좌표. La : longitude, Ma : latitude
	const bounds = map.getBounds(); // 영역 정보
	const swLatLng = bounds.getSouthWest(); // 남서 꼭지점 좌표
	const neLatLng = bounds.getNorthEast(); // 북동 꼭지점 좌표

	const mapAreaInfo = {
		centerLat: center.Ma, // 중심 latitude
		centerLon: center.La, // 중심 longitude
		swLat: swLatLng.Ma,
		swLon: swLatLng.La,
		neLat: neLatLng.Ma,
		neLon: neLatLng.La,
	};

	const url = "http://" + location.host + "/enjoytrip/tour"; // 여기에 API url.
	const fetchOption = {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(mapAreaInfo),
	};

	const response = await fetch(url, fetchOption);
	return response.json();
}

// 가져온 정보들이 json으로 파싱돼있으므로 이를 맵 객체에 담는다.
async function makeMarkPositions() {
	positionInit();
	const response = await getAreaInfo();

	const contents = { 12: 0, 32: 1, 39: 2, 14: 3, 15: 4, 16: 5 };

	for (item of response.response.body.items.item) {
		// console.log(item);
		// contentType에 따라 markPositions에 분류해서 넣는다.
		// 12: 관광지, 32: 숙박, 39: 음식점, 14: 문화시설, 15: 공연, 38: 쇼핑
		const ind = contents[item.contentTypeId]; // 12 => 0, 32 => 1
		markPositions[ind].push({
			title: item.title,
			addr1: item.addr1,
			image: item.firstimage,
			tel: item.tel,
			latlng: new kakao.maps.LatLng(item.mapy, item.mapx),
		});
	}
	// console.log(markPositions);
	makeMarkMarkers(); // 가져온 객체들로 마커 생성하기
}

/** 테스트용 드라이버 시작*/
async function testDriver() {
	// 공공데이터 api로 예시 데이터 가져와보기
	const response = await fetch(
		"https://apis.data.go.kr/B551011/KorService1/locationBasedList1?serviceKey=Ee0EApL3oZ3hp4%2BOc13AHLXzxi8eCyupR6C1Od3o12ybTMrN%2F3PuLRSVWpaAoNkZtosEJERnAV%2BCmhPyo8d5Jw%3D%3D&numOfRows=10&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&listYN=Y&arrange=A&mapX=126.981611&mapY=37.568477&radius=1000&contentTypeId=15"
	);
	return response.json();
}

// 잘 들어가는지 테스트
async function makeMarkPositionsTest() {
	positionInit();
	const response = await testDriver();
	const contents = { 12: 0, 32: 1, 39: 2, 14: 3, 15: 4, 16: 5 };

	for (item of response.response.body.items.item) {
		console.log(item);
		// contentType에 따라 markPositions에 분류해서 넣는다.
		// 12: 관광지, 32: 숙박, 39: 음식점, 14: 문화시설, 15: 공연, 38: 쇼핑
		const ind = contents[item.contenttypeid]; // 12 => 0, 32 => 1
		// markPositions[ind].push(new kakao.maps.LatLng(item.mapy, item.mapx));
		markPositions[ind].push({
			title: item.title,
			addr1: item.addr1,
			image: item.firstimage,
			tel: item.tel,
			latlng: new kakao.maps.LatLng(item.mapy, item.mapx),
		});
	}
	console.log(markPositions);
	console.log(markPositions[4][0]);
	makeMarkMarkers();
}
/** 테스트용 드라이버 끝 */

const imgName = [
	"binoculars-fill.svg",
	"house-fill.svg",
	"egg-fried.svg",
	"book-half.svg",
	"music-note-beamed.svg",
	"cart-fill.svg",
	"bezier2.svg",
];

// markMarkers 채우기
function makeMarkMarkers() {
	for (let x = 0; x < markPositions.length; x++) {
		markMarkers[x].length = 0; // 초기화
		// 장소 위,경도 배열
		const imageSize = new kakao.maps.Size(22, 26); // 마커 이미지 사이즈
		const markerImageSrc = `./assets/img/mapMarker/${imgName[x]}`;

		// 마커 이미지 생성
		const markerImage = createMarkerImage(markerImageSrc, imageSize);
		for (let y = 0; y < markPositions[x].length; y++) {
			// 마커이미지와 위치정보로 마커를 생성합니다
			const marker = createMarker(markPositions[x][y]["latlng"], markerImage);
			// 생성된 마커를 커피숍 마커 배열에 추가합니다
			markMarkers[x].push(marker);
		}
		console.log(markMarkers[x]);
	}
}

// 각 메뉴마다 표시 이벤트 달아주기
const categoryMenuEls = document.querySelectorAll(".category li");
for (let i = 0; i < categoryMenuEls.length; i++) {
	categoryMenuEls.item(i).addEventListener("click", async () => {
		if (categoryMenuEls.item(i).classList.toggle("menu_selected")) {
			// 현재 메뉴가 선택된다면
			//   await makeMarkPositions();
			await makeMarkPositionsTest(); // 테스트용 드라이버

			for (let x = 0; x < markMarkers.length; x++) {
				if (x == i) {
					// 현재 메뉴에 해당하는 마커 표시
					for (let j = 0; j < markMarkers[x].length; j++) {
						markMarkers[x][j].setMap(map);

						// 마커에 custom Overlay 클릭 이벤트 추가
						addCustomOverlayToMarker(markMarkers[x][j], x, j);
					}
				} else {
					// 다른 마커 제거
					if (categoryMenuEls.item(x).classList.contains("menu_selected")) {
						categoryMenuEls.item(x).classList.toggle("menu_selected");
						for (let j = 0; j < markMarkers[x].length; j++) {
							markMarkers[x][j].setMap(null);
						}
					}
				}
			}
		} else {
			// 현재 메뉴가 선택해제된다면 현재 메뉴의 마커 제거
			for (let j = 0; j < markMarkers[i].length; j++) {
				markMarkers[i][j].setMap(null);
			}
		}
		// console.log("markMarkers: ", markMarkers[i]);
	});
}
// 끝 - 마커 표시 메뉴 및 마커 표시
// =============

// =============
// 시작 - 모달 창 표시
// selectedMarker 변수는 아직 미사용!
let selectedOverlay = null;

function addCustomOverlayToMarker(marker, contentIdx, itemIdx) {
	kakao.maps.event.addListener(marker, "click", function () {
		// console.log(marker);
		// console.log(itemIdx);
		// console.log(markPositions[contentIdx][itemIdx]);

		if (selectedOverlay != null) {
			selectedOverlay.setMap(null);
			selectedOverlay = null;
		}
		openOverlay(contentIdx, itemIdx);

		// 커스텀 오버레이 지도에 표시
		selectedOverlay.setMap(map);
	});
}

function closeOverlay() {
	selectedOverlay.setMap(null);
	selectedOverlay = null;
}

function openOverlay(contentIdx, itemIdx) {
	const item = markPositions[contentIdx][itemIdx];
	const image =
		item.image === "" ? "/assets/img/overlay-no-image.png" : item.image;

	// custom Overlay 내용
	var content = `
	<div class="map__area-info__wrap">
		<div class="map__area-info__info">
			<div class="map__area-info__title">
				<span>${item.title}</span>
				<div class="map__area-info__close" onclick="closeOverlay()" title="닫기">
					<i class="bi bi-x"></i>
				</div>
			</div>
			<div class="map__area-info__body"> 
				<div class="img">
					<img src="${image}" width="73" height="70">
				</div>
				<div class="map__area-info__desc">
					<div class="ellipsis">${item.addr1}</div>
					<div class="jibun ellipsis">${item.addr2}</div>
					<div>${item.tel}</div>
				</div>
			</div>
		</div>
	</div>
	<style>
		.map__area-info__wrap {
			position: absolute;
			left: 0;
			bottom: 40px;
			margin-left: -144px;
			text-align: left;
			overflow: hidden;
			font-size: 12px;
			line-height: 1.5;
			width: 30vw;
		}
		.map__area-info__wrap * {
			padding: 0;
			margin: 0;
		}
		.map__area-info__info {
			width: 100%;
			border-bottom: 2px solid #ccc;
			border-right: 1px solid #ccc;
			overflow: hidden;
		}
		.map__area-info__info:nth-child(1) {
			border: 0;
			box-shadow: 0px 1px 2px #888;
		}
		.map__area-info__title {
			padding: 5px 0 5px 10px;
			background: var(--primary);
			border-bottom: 1px solid #ddd;
			border-radius: 5px 5px 0 0;
			font-size: 18px;
			font-weight: bold;
		}
		.map__area-info__title span {
			display: block;
			width: 90%;
			max-width: 90%;
			overflow: visible;
			overflow-wrap: break-word;
		}
		.map__area-info__close {
			position: absolute;
			top: 10px;
			right: 10px;
			color: white;
			width: 17px;
			height: 17px;
			display:flex;
			justify-content: center;
			align-items: center;
			font-size: 20px;
		}
		.map__area-info__close:hover {
			cursor: pointer;
		}
		.map__area-info__body {
			position: relative;
			overflow: hidden;
			background-color: white;
			border-radius: 0 0 5px 5px;
			margin-bottom: 12px;
		}
		.map__area-info__info .map__area-info__desc {
			position: relative;
			margin: 13px 0 0 90px;
			height: 75px;
		}
		.map__area-info__desc .ellipsis {
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}
		.map__area-info__desc .jibun {
			font-size: 11px;
			color: #888;
			margin-top: -2px;
		}
		.map__area-info__info .img {
			position: absolute;
			top: 6px;
			left: 5px;
			width: 73px;
			height: 71px;
			border: 1px solid #ddd;
			color: #888;
			overflow: hidden;
		}
		.map__area-info__info:after {
			content: "";
			position: absolute;
			margin-left: -12px;
			left: 50%;
			bottom: 0;
			width: 22px;
			height: 12px;
			background: url("https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/vertex_white.png");
		}
		.map__area-info__info .link {
			color: #5085bb;
		}
	</style>
	`;

	// 커스텀 오버레이 생성
	selectedOverlay = new kakao.maps.CustomOverlay({
		position: item.latlng, // 마커를 표시할 위치
		content: content,
		xAnchor: 0.3,
		yAnchor: 0.91,
	});
}

// 끝 - 모달 창 표시
// =============

// =============
// 시작 - 검색해서

// 끝 - 모달 창 표시
// =============
