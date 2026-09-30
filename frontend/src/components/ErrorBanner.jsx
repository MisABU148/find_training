export default function ErrorBanner({ message, onClose }) {
  if (!message) return null

  return (
    <div className="error-banner" role="alert">
      <span>{message}</span>
      {onClose && (
        <button type="button" className="error-banner__close" onClick={onClose} aria-label="Закрыть">
          ×
        </button>
      )}
    </div>
  )
}
