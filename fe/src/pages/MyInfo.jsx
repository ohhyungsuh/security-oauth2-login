import axios from 'axios';
import { useEffect, useState } from 'react';

export default function MyInfo() {

    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    const accessToken = localStorage.getItem("access");
    const refreshToken = localStorage.getItem("refresh");

    const getUserInfo = () => {
        axios({
            method: "get",
            url: "http://localhost:8080/api/v1/users/my-info",
            headers: {
                "access": accessToken,
                "refresh": refreshToken
            }
        })
            .then(response => {
                console.log("내 정보 조회 성공", response);
                setUser(response.data);
            })
            .catch(error => {
                console.error("내 정보 조회 실패", error);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    useEffect(() => {
        getUserInfo();
    }, []);

    if (loading) return <p>로딩 중...</p>;

    return (
        <>
            <h1>MyInfo</h1>
            <p>내 정보 페이지</p>

            <div>
                {user ? (
                    <>
                        <p>이름: {user.name}</p>
                        <p>이메일: {user.email}</p>
                        <p>생년월일: {user.birth}</p>
                    </>
                ) : (
                    <p>사용자 정보를 불러올 수 없습니다.</p>
                )}
            </div>
        </>
    );
}
