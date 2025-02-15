import { useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from "../redux/slices/authSlice";

export default function Home() {
    const navigate = useNavigate();

    const { isAuthenticated } = useSelector((state) => state.auth);
    const dispatch = useDispatch();

    const handleLogout = () => {
        dispatch(logout());
        navigate("/");
    }

    return (
        <>
            <h1>Home</h1>
            {!isAuthenticated && (
                <>
                    <p>로그인이 필요한 서비스입니다.</p>
                    <button onClick={() => navigate("/signin")}>로그인하기</button>
                    <button onClick={() => navigate("/signup")}>회원가입</button>
                    <button onClick={() => window.location.href = "http://localhost:8080/oauth2/authorization/google"}>
                        구글 계정으로 가입
                    </button>
                </>
            )}

            {isAuthenticated && (
                <>
                    <p>로그인 되었습니다.</p>
                    <button onClick={handleLogout}>로그아웃</button>
                    <button onClick={() => navigate("/my-info")}>내 정보</button>
                </>
            )}

        </>
    )
}