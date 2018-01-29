<h1 align=center>DetectSeoul</h1>
<p align=center>DetectSeoul은 서울을 방문하는 외래 관광객을 타겟으로하여, 서울의 유명 명소들의 사진을 이용해 딥러닝을 실시한 후 사용자가 해당 명소를 사진으로 촬영하거나, 핸드폰에 저장된 사진을 이용해 조회를 하면 해당 명소에 대한 상세한 정보를 사용자에게 제공해준다. 명소 정보는 영어 및 한국어로 사용자에게 제공된다.</p>

<br>
<br>

>Contact : ldaytw@gmailc.om
<br>

## 기획 배경
한국을 방문하는 외국인의 수는 2000년 530만여 명, 2012년 1114만여 명, 2016년 1724만여 명으로 대체로 증가하고 있는 추세이다. 이에 따라 서울을 방문하고 있는 외국인의 수도 같이 증가하고 있는 모습을 보여준다. 이러한 변화에 대응하기 위해 서울시에서는 서울에 있는 다양한 장소를 인터넷 및 가이드북을 통해 명소로서 소개하고 있고, 대부분의 외래 관광객들은 서울의 관광 관련 정보를 지인 또는 인터넷 등을 통해 얻고 있다.

하지만 여기서 주목할 점은 인터넷을 통해 관광 정보를 얻는다고 응답한 비율이 40%를 넘는 반면 서울 관광 홈페이지를 “이용한 적이 없다”라고 대답한 외국인의 비율은 61%로 대부분의 관광객이 서울시에서 제공하는 정보보다는 다른 경로를 통해서 명소에 대한 정보를 얻고 있는 것을 알 수 있다. 

또한, [서울 관광 만족도 분석 정보]를 살펴보면 서울의 즐길거리가 부족하다는 의견이 5점 만점 중 3.72점으로 다른 항목에 비해 높지 않은 것을 살펴볼 수 있다.

따라서 이러한 문제점들을 보충하기 위해 서울시에서 제공하는 명소 정보를 보다 다양하게 접할 수 있고, 동시에 하나의 즐길거리로서 사용될 수 있는 융합된 콘텐츠를 개발할 필요가 있고, 위에서 언급한 문제점들의 하나의 해결방안이 될 수 있는 콘텐츠인 DetectSeoul이라는 앱을 제작하고자 한다.
DetectSeoul은 명소에 대한 이미지를 인식해 해당 명소에 대한 상세한 정보를 제공해주는 앱으로써 서울 명소 사진을 딥러닝을 통해 사전에 학습한 후 이 데이터를 이용해 카메라, 핸드폰에 저장된 사진 또는 웹페이지 이미지를 통해 해당 명소를 인식 후 visitseoul.net에서 제공하는 관광정보를 이용해 사용자에게 해당 명소에 대한 상세한 정보를 제공해주는 앱이다.

카메라, 저장된 사진 및 웹페이지 이미지를 이용해 명소를 인식하고, 그에 맞는 정보를 영어 및 한국어로 보여줄 수 있는 맞춤 앱을 제작하고자 한다. 이 앱을 통해 외래 관광객들은 명소의 이름을 알지 못해도 사진 촬영을 통해 해당 명소에 대한 상세한 정보를 손쉽게 얻을 수 있을 것이며, 서울 곳곳에 숨어있는 명소를 찾아가 보고자하는 동기를 얻을 수 있을 것이다.

<br>
<br>

## 서비스 내용
DetectSeoul에서 제공하는 기능은 크게 3가지 이다. 이미지를 통해 명소를 인식하고 해당 명소에 대한 상세한 정보를 제공해주는 기능, 정보를 얻고자 하는 명소를 검색을 통해 찾는 기능, 마지막으로 앱의 메인화면에서 랜덤으로 서울의 명소를 소개해주는 기능이다.
사진을 통해 명소를 인식하는 기능은 사용자로부터 사진을 입력받은 후 딥러닝을 통해 학습한 데이터를 이용해 이미지를 인식하고, 인식 결과와 명소에 대한 정보를 사용자에게 제시해주는 기능이다. 사용자가 이미지를 입력하는 방법은 카메라를 통한 입력, 모바일에 저장된 사진을 통한 입력, 웹페이지의 이미지 URL을 통한 입력으로 3가지 방법이 있다. (2017.10.28 기준 명소 인식은 경복궁, 흥인지문, N서울타워, 선유고, 국립중앙박물관만 가능하다.)
<table>
  <tr>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-151942.png"></td>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-153423.png"></td>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-150852.png"></td>
  </tr>
  <tr>
    <td width=33% colspan=2><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/IMG_0032(2).jpg"></td>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-151056.png"></td>
  </tr>
</table>

검색을 통해 명소를 찾는 기능은 사용자가 검색어를 입력하면 이와 관련된 검색 결과를 사용자에게 제시해주고, 상세한 정보를 알고자하는 명소를 선택하면 해당 명소에 대한 상세한 정보를 제공해준다.
<table>
  <tr>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-150254.png"></td>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-150353.png"></td>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-153124.png"></td>
  </tr>
</table>

앱의 메인화면을 통한 명소 랜덤 소개는 랜덤으로 서울의 명소 중 한곳을 선택해 사용자에게 제시해준다. 서울의 숨은 명소를 발견하는데 좋은 역할을 할 수 있다.
<table>
  <tr>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-150226.png"></td>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-150151.png"></td>
    <td width=33%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-151933.png"></td>
  </tr>
</table>

추가적으로, 명소 상세정보 조회 페이지의 구성은 명소 소개, 위치, 지도, 전화번호, 웹페이지, 주차요금 등 visitseoul.net에서 얻을 수 있는 정보를 사용하여 사용자에게 제시해준다.
<table>
  <tr>
    <td width=20%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-150852.png"></td>
    <td width=20%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-151110.png"></td>
    <td width=20%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-151116.png"></td>
    <td width=20%><img src="https://github.com/pooi/DetectSeoul/blob/master/Screenshot/Screenshot_20171022-151056.png"></td>
  </tr>
</table>

<br>
<br>

## 데이터 활용 내용
<ul>
  <li>Google – 딥러닝 학습에 필요한 이미지 수집에 활용된다.</li>
  <li>visitseoul.net – 서울 명소에 대한 정보 수집에 사용된다. 이미지 인식 후 상세 정보 제공, 검색 등에 활용된다.</li>
  <li>다음카카오 지도 – 명소의 주소를 토대로 사용자에게 지도를 통해 위치를 표시해 주는 기능에 활용된다. </li>
</ul>

<br>
<br>

## 기대 효과
<ul>
  <li>이 앱이 서울뿐만 아니라 전국의 모든 관광지에 적용된다면, 한국만의 특색 있는 관광 콘텐츠를 제작하는 것이 가능하다.</li>
  <li>외국인들에게 명소에 대한 정보를 보다 쉽게 제시해 줄 수 있다.</li>
  <li>서울을 즐기는 하나의 놀이 문화로서 즐길거리를 제공해줄 수 있다.</li>
</ul>

<br>
<br>

## 출처
<ul>
  <li><a href="http://data.seoul.go.kr/openinf/linkview.jsp?infId=OA-860">http://data.seoul.go.kr/openinf/linkview.jsp?infId=OA-860</a></li>
  <li><a href="http://sculture.seoul.go.kr/files/2016/04/5714d504dd3d49.99991083.pdf">http://sculture.seoul.go.kr/files/2016/04/5714d504dd3d49.99991083.pdf</a></li>
</ul>

<br>
<br>

## License
```
    Copyright 2017 Taewoo You

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
