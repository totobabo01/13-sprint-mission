// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);

        if (!response.ok) {
            throw new Error('Failed to fetch users');
        }

        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);

        const userListElement = document.getElementById('userList');
        userListElement.textContent = '사용자 목록을 불러오지 못했습니다.';
    }
}

// Fetch user profile image
async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);

        if (!response.ok) {
            throw new Error('Failed to fetch profile');
        }

        // /api/binaryContent/find가 JSON이 아니라 실제 파일 바이트를 반환하므로 blob으로 처리
        const blob = await response.blob();

        // blob 데이터를 브라우저에서 img src로 사용할 수 있는 임시 URL로 변환
        return URL.createObjectURL(blob);
    } catch (error) {
        console.error('Error fetching profile:', error);
        return null;
    }
}

// Render user list
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    if (!users || users.length === 0) {
        const emptyMessage = document.createElement('p');
        emptyMessage.textContent = '등록된 사용자가 없습니다.';
        userListElement.appendChild(emptyMessage);
        return;
    }

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const profileUrl = user.profileId
            ? await fetchUserProfile(user.profileId)
            : null;

        const profileElement = createProfileElement(user, profileUrl);
        const userInfoElement = createUserInfoElement(user);
        const statusBadgeElement = createStatusBadgeElement(user);

        userElement.appendChild(profileElement);
        userElement.appendChild(userInfoElement);
        userElement.appendChild(statusBadgeElement);

        userListElement.appendChild(userElement);
    }
}

// Create profile image or text avatar
function createProfileElement(user, profileUrl) {
    if (profileUrl) {
        const imgElement = document.createElement('img');
        imgElement.src = profileUrl;
        imgElement.alt = user.username || 'user profile';
        imgElement.className = 'user-avatar';

        return imgElement;
    }

    const firstLetter = user.username
        ? user.username.charAt(0).toUpperCase()
        : '?';

    const avatarElement = document.createElement('div');
    avatarElement.className = 'user-avatar avatar-text';
    avatarElement.textContent = firstLetter;

    return avatarElement;
}

// Create user info area
function createUserInfoElement(user) {
    const userInfoElement = document.createElement('div');
    userInfoElement.className = 'user-info';

    const userNameElement = document.createElement('div');
    userNameElement.className = 'user-name';
    userNameElement.textContent = user.username || '';

    const userEmailElement = document.createElement('div');
    userEmailElement.className = 'user-email';
    userEmailElement.textContent = user.email || '';

    userInfoElement.appendChild(userNameElement);
    userInfoElement.appendChild(userEmailElement);

    return userInfoElement;
}

// Create online/offline badge
function createStatusBadgeElement(user) {
    const statusBadgeElement = document.createElement('div');
    statusBadgeElement.className = `status-badge ${user.online ? 'online' : 'offline'}`;
    statusBadgeElement.textContent = user.online ? '온라인' : '오프라인';

    return statusBadgeElement;
}