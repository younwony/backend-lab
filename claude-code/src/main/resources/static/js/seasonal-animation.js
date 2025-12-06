/**
 * 계절별 애니메이션 생성 스크립트
 */

// 계절별 애니메이션 생성 함수
function createSeasonalAnimation(season) {
    const animationConfig = {
        spring: {
            emoji: ['🌸', '🌺', '🌼', '🌷'],
            className: 'petal',
            count: 20,
            duration: { min: 8, max: 15 }
        },
        summer: {
            emoji: ['☀️', '🌞'],
            className: 'sun-ray',
            count: 0, // 여름은 배경만
            duration: { min: 0, max: 0 }
        },
        autumn: {
            emoji: ['🍂', '🍁', '🍃'],
            className: 'leaf',
            count: 25,
            duration: { min: 8, max: 15 }
        },
        winter: {
            emoji: ['❄️', '❅', '❆'],
            className: 'snowflake',
            count: 50,
            duration: { min: 10, max: 20 }
        }
    };

    const config = animationConfig[season];
    if (!config || config.count === 0) return;

    // 기존 애니메이션 요소 제거
    document.querySelectorAll('.snowflake, .petal, .leaf').forEach(el => el.remove());

    // 새로운 애니메이션 요소 생성
    for (let i = 0; i < config.count; i++) {
        createAnimatedElement(config);
    }
}

// 애니메이션 요소 생성
function createAnimatedElement(config) {
    const element = document.createElement('div');
    element.className = config.className;
    element.textContent = config.emoji[Math.floor(Math.random() * config.emoji.length)];

    // 랜덤 위치 설정
    element.style.left = Math.random() * 100 + 'vw';

    // 랜덤 애니메이션 지속 시간
    const duration = config.duration.min + Math.random() * (config.duration.max - config.duration.min);
    element.style.animationDuration = duration + 's';

    // 랜덤 시작 지연
    element.style.animationDelay = Math.random() * 5 + 's';

    document.body.appendChild(element);

    // 애니메이션 종료 후 재생성
    element.addEventListener('animationend', () => {
        element.remove();
        createAnimatedElement(config);
    });
}

// 페이지 로드 시 계절별 애니메이션 시작
document.addEventListener('DOMContentLoaded', function() {
    const bodyClasses = document.body.classList;
    let currentSeason = 'spring'; // 기본값

    if (bodyClasses.contains('winter')) currentSeason = 'winter';
    else if (bodyClasses.contains('spring')) currentSeason = 'spring';
    else if (bodyClasses.contains('summer')) currentSeason = 'summer';
    else if (bodyClasses.contains('autumn')) currentSeason = 'autumn';

    createSeasonalAnimation(currentSeason);
});
