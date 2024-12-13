// Firebase SDK import
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Firebase 프로젝트 설정 (Firebase 콘솔에서 제공된 구성값 사용)
const firebaseConfig = {
  apiKey: "AIzaSyDTG0BIn8Dn_5-lydSeOlWgHEe44fR16Gw",
  authDomain: "bookpli-c709a.firebaseapp.com",
  projectId: "bookpli-c709a",
  storageBucket: "bookpli-c709a.firebasestorage.app",
  messagingSenderId: "1049600608250",
  appId: "1:1049600608250:web:38f0eadc97b545885d0643",
  measurementId: "G-S5Q8Q9QYQE"
};

// Firebase 앱 초기화
const firebaseApp = initializeApp(firebaseConfig);

// Firebase 서비스 인스턴스
const app = initializeApp(firebaseConfig);
const firebaseStorage = getStorage(app); // firebase 파일저장소
export { app, firebaseStorage };