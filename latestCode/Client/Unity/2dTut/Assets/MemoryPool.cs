using UnityEngine;
using System.Collections;

// 메모리 풀 클래스 출처 : http://hyunity3d.tistory.com/195
// 2차 출처: http://eskeptor.tistory.com/86?category=928924

//-----------------------------------------------------------------------------------------
// 메모리 풀 클래스
// 용도 : 특정 게임오브젝트를 실시간으로 생성과 삭제하지 않고,
//     : 미리 생성해 둔 게임오브젝트를 재활용하는 클래스입니다.
//-----------------------------------------------------------------------------------------
//MonoBehaviour 상속 안받음. IEnumerable 상속시 foreach 사용 가능
//System.IDisposable 관리되지 않는 메모리(리소스)를 해제 함
public class MemoryPool : IEnumerable, System.IDisposable
{
    //-------------------------------------------------------------------------------------
    // 아이템 클래스
    //-------------------------------------------------------------------------------------
    class Item
    {
        public bool active; // 오브젝트가 사용하고 있는 중인지 판단하는 변수
        public GameObject gameObject; // 저장할 오브젝트
    }

    // 위의 아이템 클래스를 배열로 선언(즉, 여러개의 아이템을 저장 가능)
    Item[] table;

    //------------------------------------------------------------------------------------
    // 열거자 기본 재정의(foreach에서 사용하는 것인데 우리는 사용하지 않음)
    //------------------------------------------------------------------------------------
    public IEnumerator GetEnumerator()
    {
        if (table == null)    // 만약 table이 객체화 되지 않았다면?
            yield break;    // 함수를 그냥 탈출

        // table이 존재하면 여기서부터 실행   
        // count는 table의 길이(즉, 배열의 크기)
        int count = table.Length;

        for (int i = 0; i < count; i++)    // 총 count만큼 반복
        {
            Item item = table[i];
            // item에 table의 i위치에 해당되는 객체를 대입
            if (item.active) // item이 사용중이면
                yield return item.gameObject; // 현 item의 오브젝트를 반환
        }
    }

    //-------------------------------------------------------------------------------------
    // 메모리 풀 생성
    // original : 미리 생성해 둘 원본소스
    // count : 풀 최고 갯수
    //-------------------------------------------------------------------------------------
    public void Create(Object original, int count)
    {
        Dispose();    // 메모리풀 초기화
        table = new Item[count]; // count 만큼 배열을 생성

        for (int i = 0; i < count; i++) // count 만큼 반복
        {
            Item item = new Item();
            item.active = false;
            item.gameObject = GameObject.Instantiate(original) as GameObject;
            // original을 GameObject 형식으로 item.gameObject에 저장

            item.gameObject.SetActive(false);
            // SetActive는 활성화 함수인데 메모리에만 올릴 것이므로 비활성화 상태로 저장
            table[i] = item;
        }
    }

    public void Create(Object original, int count, Transform parent)
    {
        Dispose();    // 메모리풀 초기화
        table = new Item[count]; // count 만큼 배열을 생성

        for (int i = 0; i < count; i++) // count 만큼 반복
        {
            Item item = new Item();
            item.active = false;
            item.gameObject = GameObject.Instantiate(original) as GameObject;
            // original을 GameObject 형식으로 item.gameObject에 저장

            // parent 설정
            item.gameObject.transform.SetParent(parent, true);

            item.gameObject.SetActive(false);
            // SetActive는 활성화 함수인데 메모리에만 올릴 것이므로 비활성화 상태로 저장
            table[i] = item;
        }
    }

    //-------------------------------------------------------------------------------------
    // 새 아이템 요청 - 쉬고 있는 객체를 반납한다.
    //-------------------------------------------------------------------------------------
    public GameObject NewItem() // GetEnumerator()와 비슷
    {
        if (table == null)
            return null;
        int count = table.Length;
        for (int i = 0; i < count; i++)
        {
            Item item = table[i];
            if (item.active == false)
            {
                item.active = true;
                item.gameObject.SetActive(true);
                return item.gameObject;
            }
        }

        return null;
    }

    //--------------------------------------------------------------------------------------
    // 아이템 사용종료 - 사용하던 객체를 쉬게한다.
    // gameOBject : NewItem으로 얻었던 객체
    //--------------------------------------------------------------------------------------
    public void RemoveItem(GameObject gameObject)
    {
        // table이 객체화되지 않았거나, 매개변수로 오는 gameObject가 없다면
        if (table == null || gameObject == null)
            return; // 함수 탈출

        // table이 존재하거나, 매개변수로 오는 gameObject가 존재하면 여기서부터 실행
        // count는 table의 길이(즉, 배열의 크기)
        int count = table.Length;

        for (int i = 0; i < count; i++)
        {
            Item item = table[i];
            // 매개변수 gameObject와 item의 gameObject가 같다면
            if (item.gameObject == gameObject)
            {
                // active 변수를 false로
                item.active = false;
                // 그리고 게임오브젝트를 비활성화 시킨다.
                item.gameObject.SetActive(false);
                break;
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // 모든 아이템 사용종료 - 모든 객체를 쉬게한다.
    //--------------------------------------------------------------------------------------
    public void ClearItem()
    {
        // table이 객체화되지 않았다면..
        if (table == null)
            return;

        // table이 존재하면...
        // count는 table의 길이(즉, 배열의 크기)
        int count = table.Length;

        for (int i = 0; i < count; i++)
        {
            Item item = table[i];
            // item이 비어있지 않고, 활성화되어 있다면
            if (item != null && item.active)
            {
                // 비활성화 처리를 시작합니다.
                item.active = false;
                item.gameObject.SetActive(false);
            }
        }
    }

    //--------------------------------------------------------------------------------------
    // 메모리 풀 삭제
    //--------------------------------------------------------------------------------------
    public void Dispose()
    {
        // table이 객체화되지 않았다면..
        if (table == null)
            return;

        // table이 존재하면...
        // count는 table의 길이(즉, 배열의 크기)
        int count = table.Length;

        for (int i = 0; i < count; i++)
        {
            Item item = table[i];
            GameObject.Destroy(item.gameObject);
            // 메모리 풀을 삭제하는 것이기 때문에 모든 오브젝트를 Destroy 한다.
        }
        table = null;
    }

}


