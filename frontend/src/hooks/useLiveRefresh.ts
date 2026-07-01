import { useEffect, useRef } from 'react';

const LIVE_POLL_MS = 15000;

export function useLiveRefresh(refresh: () => void, enabled: boolean) {
  const refreshRef = useRef(refresh);
  refreshRef.current = refresh;

  useEffect(() => {
    if (!enabled) return;
    const id = setInterval(() => refreshRef.current(), LIVE_POLL_MS);
    return () => clearInterval(id);
  }, [enabled]);
}
